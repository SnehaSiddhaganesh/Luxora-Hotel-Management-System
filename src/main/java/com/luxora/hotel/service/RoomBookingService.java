package com.luxora.hotel.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.luxora.hotel.exception.InvalidBookingException;
import com.luxora.hotel.exception.RoomNotAvailableException;
import com.luxora.hotel.model.Booking;
import com.luxora.hotel.model.BookingStatus;
import com.luxora.hotel.model.Customer;
import com.luxora.hotel.model.Room;
import com.luxora.hotel.model.RoomStatus;

public class RoomBookingService {
    private final Map<String, Room> rooms = new HashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();

    public void registerRoom(Room room) {
        if (room == null) {
            throw new InvalidBookingException("Room details are required.");
        }
        rooms.put(room.getRoomNumber(), room);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Room> getAvailableRooms() {
        return rooms.values().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE || room.getStatus() == RoomStatus.RESERVED)
                .toList();
    }

    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            return false;
        }
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new InvalidBookingException("Check-in and check-out dates are invalid.");
        }
        return room.getStatus() != RoomStatus.OCCUPIED
                && room.getStatus() != RoomStatus.CLEANING
                && room.getStatus() != RoomStatus.MAINTENANCE;
    }

    public long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new InvalidBookingException("Booking duration must be a positive range.");
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public Booking bookRoom(Customer customer, String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        if (customer == null) {
            throw new InvalidBookingException("Customer is required to book a room.");
        }
        if (roomNumber == null || roomNumber.isBlank()) {
            throw new InvalidBookingException("Room number is required.");
        }
        if (!isRoomAvailable(roomNumber, checkIn, checkOut)) {
            throw new RoomNotAvailableException("Room " + roomNumber + " is not available for the selected dates.");
        }

        Room room = rooms.get(roomNumber);
        if (room == null) {
            throw new InvalidBookingException("Room " + roomNumber + " was not found.");
        }

        Booking booking = new Booking(
                "BKNG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                customer,
                room,
                checkIn,
                checkOut
        );

        booking.setStatus(BookingStatus.CONFIRMED);
        room.setStatus(RoomStatus.OCCUPIED);
        bookings.put(booking.getBookingId(), booking);
        customer.addBooking(booking);
        return booking;
    }

    public Booking findBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new InvalidBookingException("Booking not found: " + bookingId);
        }
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = findBooking(bookingId);
        booking.setStatus(BookingStatus.CANCELLED);
        booking.getRoom().setStatus(RoomStatus.AVAILABLE);
    }

    public double calculateRoomCharge(String bookingId) {
        Booking booking = findBooking(bookingId);
        return booking.getTotalAmount();
    }
}
