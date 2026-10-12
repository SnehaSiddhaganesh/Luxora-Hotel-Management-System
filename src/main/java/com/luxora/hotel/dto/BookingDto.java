package com.luxora.hotel.dto;

import java.time.LocalDate;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.model.BookingStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookingDto {

    public record CreateRequest(
            @NotNull(message = "Room id is required") Long roomId,
            @NotNull(message = "Check-in date is required") LocalDate checkInDate,
            @NotNull(message = "Check-out date is required") LocalDate checkOutDate,
            @Min(value = 1, message = "Guest count must be at least one") int numberOfGuests,
            @Size(max = 500, message = "Special requests cannot exceed 500 characters") String specialRequests
    ) {
    }

    public record StatusUpdateRequest(@NotNull(message = "Status is required") BookingStatus status) {
    }

    public record Response(
            Long id,
            String bookingCode,
            Long customerId,
            String customerName,
            Long roomId,
            String roomNumber,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            BookingStatus status,
            int numberOfGuests,
            int nights,
            java.math.BigDecimal nightlyRate,
            java.math.BigDecimal subtotal,
            java.math.BigDecimal tax,
            java.math.BigDecimal total,
            String specialRequests
    ) {
        public static Response fromEntity(Booking booking) {
            if (booking == null) {
                return null;
            }

            String customerName = booking.getCustomer() == null
                    ? null
                    : booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName();

            String roomNumber = booking.getRoom() == null ? null : booking.getRoom().getRoomNumber();

            return new Response(
                    booking.getId(),
                    booking.getBookingCode(),
                    booking.getCustomer() != null ? booking.getCustomer().getId() : null,
                    customerName,
                    booking.getRoom() != null ? booking.getRoom().getId() : null,
                    roomNumber,
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getStatus(),
                    booking.getNumberOfGuests(),
                    booking.getNights(),
                    booking.getNightlyRate(),
                    booking.getSubtotal(),
                    booking.getTax(),
                    booking.getTotal(),
                    booking.getSpecialRequests()
            );
        }
    }
}
