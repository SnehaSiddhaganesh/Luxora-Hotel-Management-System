package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.luxora.hotel.entity.Payment;
import com.luxora.hotel.model.PaymentStatus;
import com.luxora.hotel.model.PaymentType;

public interface PaymentService {
    Payment createPayment(Long bookingId, PaymentType paymentType, BigDecimal amount);
    Optional<Payment> getPaymentByBookingId(Long bookingId);
    List<Payment> getPaymentsByStatus(PaymentStatus status);
    Payment updatePaymentStatus(Long paymentId, PaymentStatus status);
    Payment processPayment(Long bookingId, PaymentType paymentType, BigDecimal amount);
}
