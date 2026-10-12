package com.luxora.hotel.security;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.repository.BookingRepository;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.repository.OrderRepository;
import com.luxora.hotel.repository.PaymentRepository;

class CustomerAccessServiceTest {

    @AfterEach
    void clearSecurityContext() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void customerCanReadOwnRecordsButNotAnotherCustomersRecords() {
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        CustomerAccessService accessService = new CustomerAccessService(customerRepository,
                mock(BookingRepository.class), mock(OrderRepository.class), mock(PaymentRepository.class));
        Customer customer = new Customer("Grace", "Guest", "grace@example.com", null, null);
        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(8L)).thenReturn(Optional.of(
                new Customer("Alex", "Guest", "alex@example.com", null, null)));
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("grace@example.com", "N/A",
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));

        assertDoesNotThrow(() -> accessService.assertCustomerAccess(7L));
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
            () -> accessService.assertCustomerAccess(8L));
        assertEquals("You may access only your own hotel records", exception.getMessage());
    }
}