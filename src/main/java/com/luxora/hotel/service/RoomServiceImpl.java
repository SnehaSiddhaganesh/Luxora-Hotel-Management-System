package com.luxora.hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.entity.Room;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.model.BookingStatus;
import com.luxora.hotel.model.RoomStatus;
import com.luxora.hotel.repository.BookingRepository;
import com.luxora.hotel.repository.RoomRepository;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomServiceImpl(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Room saveRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room details are required.");
        }
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank()) {
            throw new IllegalArgumentException("Room number is required.");
        }
        if (room.getPricePerNight() <= 0) {
            throw new IllegalArgumentException("Room price must be greater than zero.");
        }
        return roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Room> getRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Room> getRoomByNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.isBlank()) {
            return Optional.empty();
        }
        return roomRepository.findByRoomNumber(roomNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAvailableRooms() {
        return roomRepository.findAll().stream()
                .filter(room -> room.getStatus() != RoomStatus.OCCUPIED
                        && room.getStatus() != RoomStatus.CLEANING
                        && room.getStatus() != RoomStatus.MAINTENANCE)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAvailableRoomsForDates(LocalDate checkInDate, LocalDate checkOutDate,
                                                String roomType, Integer guests) {
        if ((checkInDate == null) != (checkOutDate == null)) {
            throw new IllegalArgumentException("Both check-in and check-out dates are required.");
        }
        if (guests != null && guests < 1) {
            throw new IllegalArgumentException("Guest count must be at least one.");
        }

        return roomRepository.findAll().stream()
                .filter(room -> roomType == null || roomType.isBlank()
                        || room.getRoomType().equalsIgnoreCase(roomType.trim()))
                .filter(room -> guests == null || room.getMaxGuests() >= guests)
                .filter(room -> checkInDate == null
                        ? room.getStatus() != RoomStatus.OCCUPIED
                                && room.getStatus() != RoomStatus.CLEANING
                                && room.getStatus() != RoomStatus.MAINTENANCE
                        : isRoomAvailableForDates(room.getId(), checkInDate, checkOutDate))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByType(String roomType) {
        if (roomType == null || roomType.isBlank()) {
            return roomRepository.findAll();
        }
        return roomRepository.findByRoomType(roomType);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRoomAvailableForDates(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (roomId == null) {
            throw new IllegalArgumentException("Room id is required.");
        }
        if (checkInDate == null || checkOutDate == null || checkInDate.isBefore(LocalDate.now())
                || !checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-in must be today or later and check-out must be after check-in.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        if (room.getStatus() == RoomStatus.MAINTENANCE || room.getStatus() == RoomStatus.CLEANING
                || (room.getStatus() == RoomStatus.OCCUPIED && !checkInDate.isAfter(LocalDate.now()))) {
            return false;
        }

        boolean hasConflict = bookingRepository.existsByRoomAndStatusNotInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                room,
                List.of(BookingStatus.CANCELLED, BookingStatus.CHECKED_OUT),
                checkOutDate,
                checkInDate
        );

        return !hasConflict;
    }

    @Override
    @Transactional
    public Room updateRoomStatus(Long roomId, RoomStatus status) {
        if (roomId == null) {
            throw new IllegalArgumentException("Room id is required.");
        }
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        room.setStatus(status);
        return roomRepository.save(room);
    }
}
