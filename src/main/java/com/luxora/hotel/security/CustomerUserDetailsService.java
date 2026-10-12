package com.luxora.hotel.security;

import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.repository.CustomerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    public CustomerUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email.trim().toLowerCase())
                .filter(account -> account.getPasswordHash() != null && !account.getPasswordHash().isBlank())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        return User.withUsername(customer.getEmail())
                .password(customer.getPasswordHash())
                .roles(customer.getRole().name())
                .build();
    }
}