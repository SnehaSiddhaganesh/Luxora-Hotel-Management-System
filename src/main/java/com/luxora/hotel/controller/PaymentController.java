package com.luxora.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luxora.hotel.dto.PaymentDto;
import com.luxora.hotel.entity.Payment;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.model.PaymentStatus;
import com.luxora.hotel.repository.PaymentRepository;
import com.luxora.hotel.security.CustomerAccessService;
import com.luxora.hotel.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final CustomerAccessService customerAccessService;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository,
                             CustomerAccessService customerAccessService) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.customerAccessService = customerAccessService;
    }

    @GetMapping("/{id}")
    public PaymentDto.Response getPaymentById(@PathVariable Long id) {
        customerAccessService.assertPaymentAccess(id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return PaymentDto.Response.fromEntity(payment);
    }

    @GetMapping("/booking/{bookingId}")
    public PaymentDto.Response getPaymentByBookingId(@PathVariable Long bookingId) {
        customerAccessService.assertBookingAccess(bookingId);
        Payment payment = paymentService.getPaymentByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking id: " + bookingId));
        return PaymentDto.Response.fromEntity(payment);
    }

    @GetMapping
    public List<PaymentDto.Response> getPaymentsByStatus(@RequestParam(required = false) PaymentStatus status) {
        if (status == null) {
            return paymentRepository.findAll().stream().map(PaymentDto.Response::fromEntity).toList();
        }

        return paymentService.getPaymentsByStatus(status).stream().map(PaymentDto.Response::fromEntity).toList();
    }

    @PostMapping
    public ResponseEntity<PaymentDto.Response> createPayment(@Valid @RequestBody PaymentDto.CreateRequest request) {
        customerAccessService.assertBookingAccess(request.bookingId());
        Payment payment = paymentService.createPayment(
                request.bookingId(),
                request.paymentType(),
                request.amount()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentDto.Response.fromEntity(payment));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentDto.Response> updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentDto.StatusUpdateRequest request
    ) {
        Payment payment = paymentService.updatePaymentStatus(id, request.status());
        return ResponseEntity.ok(PaymentDto.Response.fromEntity(payment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }

        paymentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
