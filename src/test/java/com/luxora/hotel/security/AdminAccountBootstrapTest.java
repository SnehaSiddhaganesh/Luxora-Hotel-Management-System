package com.luxora.hotel.security;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.model.UserRole;
import com.luxora.hotel.repository.CustomerRepository;

class AdminAccountBootstrapTest {

    private static final String PASSWORD = "StrongAdminPassphrase!2026";

    @Test
    void createsAdminWithHashedPasswordWhenConfiguredAccountDoesNotExist() {
        CustomerRepository repository = mock(CustomerRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(repository.findByEmail("admin@luxora.com")).thenReturn(Optional.empty());
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bootstrap(repository, passwordEncoder, " ADMIN@LUXORA.COM ", PASSWORD)
                .run(mock(ApplicationArguments.class));

        var captor = org.mockito.ArgumentCaptor.forClass(Customer.class);
        verify(repository).save(captor.capture());
        Customer savedAdmin = captor.getValue();
        assertEquals("admin@luxora.com", savedAdmin.getEmail());
        assertEquals(UserRole.ADMIN, savedAdmin.getRole());
        assertNotEquals(PASSWORD, savedAdmin.getPasswordHash());
        assertTrue(passwordEncoder.matches(PASSWORD, savedAdmin.getPasswordHash()));
    }

    @Test
    void refusesToPromoteAnExistingCustomer() {
        CustomerRepository repository = mock(CustomerRepository.class);
        Customer existingCustomer = new Customer("Casey", "Guest", "admin@luxora.com", null, null);
        when(repository.findByEmail("admin@luxora.com")).thenReturn(Optional.of(existingCustomer));

        assertThrows(IllegalStateException.class,
                () -> bootstrap(repository, new BCryptPasswordEncoder(), "admin@luxora.com", PASSWORD)
                        .run(mock(ApplicationArguments.class)));
        verify(repository, never()).save(any(Customer.class));
        assertEquals(UserRole.CUSTOMER, existingCustomer.getRole());
    }

    @Test
    void updatesPasswordForAnExistingStaffAccountWhenConfigured() {
        CustomerRepository repository = mock(CustomerRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Customer existingAdmin = new Customer("Luxora", "Administrator", "admin@luxora.com", null, null);
        existingAdmin.setRole(UserRole.ADMIN);
        existingAdmin.setPasswordHash(passwordEncoder.encode("PreviousAdminPassword!2025"));
        when(repository.findByEmail("admin@luxora.com")).thenReturn(Optional.of(existingAdmin));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bootstrap(repository, passwordEncoder, "admin@luxora.com", PASSWORD)
                .run(mock(ApplicationArguments.class));

        verify(repository).save(existingAdmin);
        assertTrue(passwordEncoder.matches(PASSWORD, existingAdmin.getPasswordHash()));
    }

    @Test
    void rejectsIncompleteOrWeakAdminConfiguration() {
        CustomerRepository repository = mock(CustomerRepository.class);
        AdminAccountBootstrap missingPassword = bootstrap(repository, new BCryptPasswordEncoder(), "admin@luxora.com", "");
        AdminAccountBootstrap weakPassword = bootstrap(repository, new BCryptPasswordEncoder(), "admin@luxora.com", "short");

        assertThrows(IllegalStateException.class,
                () -> missingPassword.run(mock(ApplicationArguments.class)));
        assertThrows(IllegalStateException.class,
                () -> weakPassword.run(mock(ApplicationArguments.class)));
        verify(repository, never()).save(any(Customer.class));
    }

    private AdminAccountBootstrap bootstrap(CustomerRepository repository, BCryptPasswordEncoder passwordEncoder,
                                            String email, String password) {
        return new AdminAccountBootstrap(repository, passwordEncoder, email, password, "Luxora", "Administrator");
    }
}
