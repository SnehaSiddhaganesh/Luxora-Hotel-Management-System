package com.luxora.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.luxora.hotel.entity.Payment;
import com.luxora.hotel.model.PaymentStatus;
import com.luxora.hotel.model.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentDto {

    public record CreateRequest(
            @NotNull(message = "Booking id is required") Long bookingId,
            @NotNull(message = "Payment type is required") PaymentType paymentType,
            @NotNull(message = "Amount is required") @Positive(message = "Amount must be greater than zero") BigDecimal amount
    ) {
    }

    public record StatusUpdateRequest(@NotNull(message = "Status is required") PaymentStatus status) {
    }

    public record Response(
            Long id,
            Long bookingId,
            PaymentType paymentType,
            PaymentStatus status,
            BigDecimal amount,
            LocalDateTime paidAt,
            String transactionReference
    ) {
        public static Response fromEntity(Payment payment) {
            if (payment == null) {
                return null;
            }

            return new Response(
                    payment.getId(),
                    payment.getBooking() != null ? payment.getBooking().getId() : null,
                    payment.getPaymentType(),
                    payment.getStatus(),
                    payment.getAmount(),
                    payment.getPaidAt(),
                    payment.getTransactionReference()
            );
        }
    }
}
