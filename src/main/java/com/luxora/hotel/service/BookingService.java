package com.luxora.hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.model.BookingStatus;

public interface BookingService {
    Booking createBooking(Long customerId, Long roomId, LocalDate checkInDate, LocalDate checkOutDate,
                          int numberOfGuests, String specialRequests);
    Optional<Booking> getBookingById(Long bookingId);
    List<Booking> getBookingsForCustomer(Long customerId);
    List<Booking> getBookingsByStatus(BookingStatus status);
    Booking updateBookingStatus(Long bookingId, BookingStatus status);
    Booking cancelBooking(Long bookingId);
}
