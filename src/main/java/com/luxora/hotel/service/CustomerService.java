package com.luxora.hotel.service;

import java.util.List;

import com.luxora.hotel.entity.Customer;

public interface CustomerService {
    Customer registerCustomer(Customer customer);
    Customer getCustomerById(Long customerId);
    Customer getCustomerByEmail(String email);
    List<Customer> getAllCustomers();
    List<Customer> searchCustomers(String keyword);
}
