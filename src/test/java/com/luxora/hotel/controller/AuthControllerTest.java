package com.luxora.hotel.controller;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import com.luxora.hotel.dto.AuthDto;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.model.UserRole;
import com.luxora.hotel.repository.CustomerRepository;

class AuthControllerTest {

    @Test
    void registrationStoresBcryptHashAndAssignsCustomerRole() {
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(customerRepository.findByEmail("ada@example.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(42L);
            return customer;
        });

        AuthController controller = new AuthController(customerRepository, passwordEncoder,
                mock(AuthenticationManager.class), mock(SecurityContextRepository.class),
                mock(SessionAuthenticationStrategy.class));
        String rawPassword = "LongPassphrase!123";

        var response = controller.register(new AuthDto.RegisterRequest(
                "Ada", "Guest", "ADA@example.com", "+1 555 0100", null, rawPassword));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(UserRole.CUSTOMER, response.getBody().role());
        ArgumentCaptor<Customer> savedCustomerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(savedCustomerCaptor.capture());
        Customer savedCustomer = savedCustomerCaptor.getValue();
        assertNotEquals(rawPassword, savedCustomer.getPasswordHash());
        assertTrue(passwordEncoder.matches(rawPassword, savedCustomer.getPasswordHash()));
    }
}