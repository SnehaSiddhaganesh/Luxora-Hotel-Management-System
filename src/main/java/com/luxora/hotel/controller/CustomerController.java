package com.luxora.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.dto.CustomerDto;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<CustomerDto.Response> getAllCustomers(
            @RequestParam(required = false) String keyword
    ) {
        List<Customer> customers = (keyword == null || keyword.isBlank())
                ? customerService.getAllCustomers()
                : customerService.searchCustomers(keyword);

        return customers.stream().map(CustomerDto.Response::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public CustomerDto.Response getCustomerById(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return CustomerDto.Response.fromEntity(customer);
    }

    @GetMapping("/email/{email}")
    public CustomerDto.Response getCustomerByEmail(@PathVariable String email) {
        Customer customer = customerService.getCustomerByEmail(email);
        return CustomerDto.Response.fromEntity(customer);
    }

    @PostMapping
    public ResponseEntity<CustomerDto.Response> createCustomer(@Valid @RequestBody CustomerDto.CreateRequest request) {
        Customer customer = new Customer();
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());

        Customer savedCustomer = customerService.registerCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDto.Response.fromEntity(savedCustomer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto.Response> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDto.UpdateRequest request
    ) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setEmail(request.email());
        existing.setPhone(request.phone());
        existing.setAddress(request.address());

        Customer updatedCustomer = customerRepository.save(existing);
        return ResponseEntity.ok(CustomerDto.Response.fromEntity(updatedCustomer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }

        customerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
