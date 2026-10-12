package com.luxora.hotel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer registerCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer details are required.");
        }
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            throw new IllegalArgumentException("Customer email is required.");
        }
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Customer with email already exists: " + customer.getEmail());
        }
        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return customerRepository.findAll();
        }
        List<Customer> matches = customerRepository.findByFirstNameContainingIgnoreCase(keyword);
        matches.addAll(customerRepository.findByLastNameContainingIgnoreCase(keyword));
        return matches.stream().distinct().toList();
    }
}
