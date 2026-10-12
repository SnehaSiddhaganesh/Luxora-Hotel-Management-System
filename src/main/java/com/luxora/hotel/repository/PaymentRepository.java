package com.luxora.hotel.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.Payment;
import com.luxora.hotel.model.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking(Booking booking);
    Optional<Payment> findByBookingAndStatus(Booking booking, PaymentStatus status);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByStatusIn(Collection<PaymentStatus> statuses);
    List<Payment> findByBookingCustomerOrderByPaidAtDesc(Customer customer);
}