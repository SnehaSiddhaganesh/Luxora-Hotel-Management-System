package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Payment;
import com.luxora.hotel.model.BookingStatus;
import com.luxora.hotel.model.PaymentStatus;
import com.luxora.hotel.model.PaymentType;
import com.luxora.hotel.repository.BookingRepository;
import com.luxora.hotel.repository.PaymentRepository;
import com.luxora.hotel.exception.InvalidPaymentException;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Payment createPayment(Long bookingId, PaymentType paymentType, BigDecimal amount) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking id is required.");
        }
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type is required.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidPaymentException("Only confirmed bookings can be paid.");
        }
        if (booking.getTotal() == null || amount.compareTo(booking.getTotal()) != 0) {
            throw new InvalidPaymentException("Payment amount must match the booking total.");
        }
        if (paymentRepository.findByBooking(booking).isPresent()) {
            throw new InvalidPaymentException("A payment already exists for this booking.");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentType(paymentType);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionReference("SIMULATED-TXN-" + System.currentTimeMillis());

        Payment saved = paymentRepository.save(payment);
        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByBookingId(Long bookingId) {
        if (bookingId == null) {
            return Optional.empty();
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElse(null);
        if (booking == null) {
            return Optional.empty();
        }
        return paymentRepository.findByBooking(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + paymentId));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment processPayment(Long bookingId, PaymentType paymentType, BigDecimal amount) {
        return createPayment(bookingId, paymentType, amount);
    }
}
