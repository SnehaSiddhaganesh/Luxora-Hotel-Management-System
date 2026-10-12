package com.luxora.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luxora.hotel.dto.BookingDto;
import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.model.BookingStatus;
import com.luxora.hotel.repository.BookingRepository;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.security.CustomerAccessService;
import com.luxora.hotel.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CustomerAccessService customerAccessService;
    private final CustomerRepository customerRepository;

    public BookingController(BookingService bookingService, BookingRepository bookingRepository,
                             CustomerAccessService customerAccessService, CustomerRepository customerRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.customerAccessService = customerAccessService;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<BookingDto.Response> getAllBookings(@RequestParam(required = false) BookingStatus status) {
        if (status != null) {
            return bookingService.getBookingsByStatus(status).stream().map(BookingDto.Response::fromEntity).toList();
        }

        return bookingRepository.findAll().stream().map(BookingDto.Response::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public BookingDto.Response getBookingById(@PathVariable Long id) {
        customerAccessService.assertBookingAccess(id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return BookingDto.Response.fromEntity(booking);
    }

    @GetMapping("/customer/{customerId}")
    public List<BookingDto.Response> getBookingsForCustomer(@PathVariable Long customerId) {
        customerAccessService.assertCustomerAccess(customerId);
        return bookingService.getBookingsForCustomer(customerId).stream().map(BookingDto.Response::fromEntity).toList();
    }

    @GetMapping("/mine")
    public List<BookingDto.Response> getMyBookings(Authentication authentication) {
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found."));
        return bookingService.getBookingsForCustomer(customer.getId()).stream().map(BookingDto.Response::fromEntity).toList();
    }

    @PostMapping
    public ResponseEntity<BookingDto.Response> createBooking(
            @Valid @RequestBody BookingDto.CreateRequest request,
            Authentication authentication
    ) {
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found."));
        Booking booking = bookingService.createBooking(
                customer.getId(),
                request.roomId(),
                request.checkInDate(),
                request.checkOutDate(),
                request.numberOfGuests(),
                request.specialRequests()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(BookingDto.Response.fromEntity(booking));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingDto.Response> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingDto.StatusUpdateRequest request
    ) {
        Booking booking = bookingService.updateBookingStatus(id, request.status());
        return ResponseEntity.ok(BookingDto.Response.fromEntity(booking));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingDto.Response> cancelBooking(@PathVariable Long id) {
        customerAccessService.assertBookingAccess(id);
        Booking booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(BookingDto.Response.fromEntity(booking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }

        bookingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
