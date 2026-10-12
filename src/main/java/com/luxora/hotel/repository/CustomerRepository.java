package com.luxora.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);
    List<Customer> findByFirstNameContainingIgnoreCase(String firstName);
    List<Customer> findByLastNameContainingIgnoreCase(String lastName);
}
