package com.luxora.hotel.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.Payment;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.repository.BookingRepository;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.repository.OrderRepository;
import com.luxora.hotel.repository.PaymentRepository;

@Service
public class CustomerAccessService {
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public CustomerAccessService(CustomerRepository customerRepository, BookingRepository bookingRepository,
                                 OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public void assertCustomerAccess(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        assertOwner(customer);
    }

    @Transactional(readOnly = true)
    public void assertBookingAccess(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        assertOwner(booking.getCustomer());
    }

    @Transactional(readOnly = true)
    public void assertOrderAccess(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        assertOwner(order.getCustomer());
    }

    @Transactional(readOnly = true)
    public void assertPaymentAccess(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        assertOwner(payment.getBooking().getCustomer());
    }

    private void assertOwner(Customer customer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Authentication is required to access hotel records");
        }

        boolean staff = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")
                        || authority.getAuthority().equals("ROLE_RECEPTIONIST"));
        if (!staff && !customer.getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new AccessDeniedException("You may access only your own hotel records");
        }
    }
}