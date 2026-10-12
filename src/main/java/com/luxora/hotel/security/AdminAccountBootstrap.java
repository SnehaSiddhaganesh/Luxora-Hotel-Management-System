package com.luxora.hotel.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.model.UserRole;
import com.luxora.hotel.repository.CustomerRepository;

@Component
public class AdminAccountBootstrap implements ApplicationRunner {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;

    public AdminAccountBootstrap(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            @Value("${luxora.bootstrap-admin.email:}") String email,
            @Value("${luxora.bootstrap-admin.password:}") String password,
            @Value("${luxora.bootstrap-admin.first-name:Luxora}") String firstName,
            @Value("${luxora.bootstrap-admin.last-name:Administrator}") String lastName
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean emailConfigured = email != null && !email.isBlank();
        boolean passwordConfigured = password != null && !password.isBlank();

        if (!emailConfigured && !passwordConfigured) {
            return;
        }
        if (!emailConfigured || !passwordConfigured || password.length() < 12 || password.length() > 72) {
            throw new IllegalStateException(
                    "Configure both LUXORA_ADMIN_EMAIL and LUXORA_ADMIN_PASSWORD; the password must be 12 to 72 characters."
            );
        }

        String normalizedEmail = email.trim().toLowerCase();
        customerRepository.findByEmail(normalizedEmail).ifPresentOrElse(existing -> {
            if (existing.getRole() != UserRole.ADMIN && existing.getRole() != UserRole.RECEPTIONIST) {
                throw new IllegalStateException(
                        "The configured bootstrap admin email belongs to a non-staff account; refusing to promote it."
                );
            }
            if (existing.getPasswordHash() == null
                    || !passwordEncoder.matches(password, existing.getPasswordHash())) {
                existing.setPasswordHash(passwordEncoder.encode(password));
                customerRepository.save(existing);
            }
        }, () -> {
            Customer admin = new Customer(firstName.trim(), lastName.trim(), normalizedEmail, null, null);
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setRole(UserRole.ADMIN);
            customerRepository.save(admin);
        });
    }
}