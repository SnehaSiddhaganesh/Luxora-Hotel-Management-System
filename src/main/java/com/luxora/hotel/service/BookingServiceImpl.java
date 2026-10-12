package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.Room;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.exception.RoomNotAvailableException;
import com.luxora.hotel.model.BookingStatus;
import com.luxora.hotel.repository.BookingRepository;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.repository.RoomRepository;

@Service
public class BookingServiceImpl implements BookingService {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;
    private final RoomService roomService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                             CustomerRepository customerRepository,
                             RoomRepository roomRepository,
                             RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
        this.roomService = roomService;
    }

    @Override
    @Transactional
    public Booking createBooking(Long customerId, Long roomId, LocalDate checkInDate, LocalDate checkOutDate,
                                 int numberOfGuests, String specialRequests) {
        if (customerId == null || roomId == null) {
            throw new IllegalArgumentException("Customer and room are required.");
        }
        if (checkInDate == null || checkOutDate == null || checkInDate.isBefore(LocalDate.now())
                || !checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-in must be today or later and check-out must be after check-in.");
        }
        if (numberOfGuests < 1) {
            throw new IllegalArgumentException("Guest count must be at least one.");
        }
        if (specialRequests != null && specialRequests.length() > 500) {
            throw new IllegalArgumentException("Special requests cannot exceed 500 characters.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Room room = roomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        if (numberOfGuests > room.getMaxGuests()) {
            throw new IllegalArgumentException("This room can accommodate at most " + room.getMaxGuests() + " guests.");
        }
        if (!roomService.isRoomAvailableForDates(roomId, checkInDate, checkOutDate)) {
            throw new RoomNotAvailableException("Room is not available for the selected dates.");
        }

        int nights = Math.toIntExact(ChronoUnit.DAYS.between(checkInDate, checkOutDate));
        BigDecimal nightlyRate = BigDecimal.valueOf(room.getPricePerNight()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = nightlyRate.multiply(BigDecimal.valueOf(nights)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);

        Booking booking = new Booking();
        booking.setBookingCode("BKNG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setNights(nights);
        booking.setNightlyRate(nightlyRate);
        booking.setSubtotal(subtotal);
        booking.setTax(tax);
        booking.setTotal(subtotal.add(tax));
        booking.setSpecialRequests(specialRequests == null || specialRequests.isBlank() ? null : specialRequests.trim());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);
        room.setStatus(com.luxora.hotel.model.RoomStatus.RESERVED);
        roomRepository.save(room);
        return savedBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return bookingRepository.findByCustomerOrderByCheckInDateDesc(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Booking status is required.");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        if (!isAllowedTransition(booking.getStatus(), status)) {
            throw new IllegalArgumentException("Cannot change booking status from " + booking.getStatus() + " to " + status + ".");
        }
        if (status == BookingStatus.CHECKED_IN && (LocalDate.now().isBefore(booking.getCheckInDate())
                || !LocalDate.now().isBefore(booking.getCheckOutDate()))) {
            throw new IllegalArgumentException("Booking can only be checked in during the reserved stay dates.");
        }
        if (status == BookingStatus.CHECKED_OUT && LocalDate.now().isBefore(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Booking cannot be checked out before the check-out date.");
        }
        booking.setStatus(status);
        Booking saved = bookingRepository.save(booking);
        reconcileRoomStatus(booking.getRoom());
        return saved;
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.CHECKED_OUT
                || booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new IllegalArgumentException("This booking can no longer be cancelled.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        reconcileRoomStatus(booking.getRoom());
        return saved;
    }

    private boolean isAllowedTransition(BookingStatus current, BookingStatus next) {
        if (current == next) {
            return true;
        }
        return switch (current) {
            case PENDING -> next == BookingStatus.CONFIRMED || next == BookingStatus.CANCELLED;
            case CONFIRMED -> next == BookingStatus.PAID || next == BookingStatus.CHECKED_IN || next == BookingStatus.CANCELLED;
            case PAID -> next == BookingStatus.CHECKED_IN || next == BookingStatus.CANCELLED;
            case CHECKED_IN -> next == BookingStatus.CHECKED_OUT;
            case CHECKED_OUT, CANCELLED -> false;
        };
    }

    private void reconcileRoomStatus(Room room) {
        if (room.getStatus() == com.luxora.hotel.model.RoomStatus.MAINTENANCE
                || room.getStatus() == com.luxora.hotel.model.RoomStatus.CLEANING) {
            return;
        }

        List<Booking> activeBookings = bookingRepository.findByRoom(room).stream()
                .filter(existing -> !EnumSet.of(BookingStatus.CANCELLED, BookingStatus.CHECKED_OUT).contains(existing.getStatus()))
                .toList();
        if (activeBookings.stream().anyMatch(existing -> existing.getStatus() == BookingStatus.CHECKED_IN)) {
            room.setStatus(com.luxora.hotel.model.RoomStatus.OCCUPIED);
        } else if (!activeBookings.isEmpty()) {
            room.setStatus(com.luxora.hotel.model.RoomStatus.RESERVED);
        } else {
            room.setStatus(com.luxora.hotel.model.RoomStatus.AVAILABLE);
        }
        roomRepository.save(room);
    }
}
