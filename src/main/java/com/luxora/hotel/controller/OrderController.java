package com.luxora.hotel.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luxora.hotel.dto.OrderDto;
import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.OrderItem;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.repository.BookingRepository;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.repository.FoodItemRepository;
import com.luxora.hotel.repository.OrderRepository;
import com.luxora.hotel.security.CustomerAccessService;
import com.luxora.hotel.service.OrderService;
import com.luxora.hotel.model.UserRole;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final FoodItemRepository foodItemRepository;
        private final CustomerAccessService customerAccessService;

    public OrderController(OrderService orderService,
                          OrderRepository orderRepository,
                          CustomerRepository customerRepository,
                          BookingRepository bookingRepository,
                          FoodItemRepository foodItemRepository,
                          CustomerAccessService customerAccessService) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.foodItemRepository = foodItemRepository;
        this.customerAccessService = customerAccessService;
    }

    @GetMapping("/{id}")
    public OrderDto.Response getOrderById(@PathVariable Long id) {
                customerAccessService.assertOrderAccess(id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderDto.Response.fromEntity(order);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderDto.Response> getOrdersForCustomer(@PathVariable Long customerId) {
                customerAccessService.assertCustomerAccess(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        return orderRepository.findByCustomerOrderByOrderedAtDesc(customer)
                .stream()
                .map(OrderDto.Response::fromEntity)
                .toList();
    }

    @GetMapping("/mine")
    public List<OrderDto.Response> getMyOrders(Authentication authentication) {
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found."));
        if (customer.getRole() != UserRole.CUSTOMER) {
            throw new AccessDeniedException("Only customers can view customer dining orders.");
        }

        customerAccessService.assertCustomerAccess(customer.getId());
        return orderRepository.findByCustomerOrderByOrderedAtDesc(customer)
                .stream()
                .map(OrderDto.Response::fromEntity)
                .toList();
    }

    @GetMapping("/booking/{bookingId}")
    public List<OrderDto.Response> getOrdersForBooking(@PathVariable Long bookingId) {
                customerAccessService.assertBookingAccess(bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        return orderRepository.findByBookingOrderByOrderedAtDesc(booking)
                .stream()
                .map(OrderDto.Response::fromEntity)
                .toList();
    }

    @PostMapping
    public ResponseEntity<OrderDto.Response> createOrder(@Valid @RequestBody OrderDto.CreateRequest request) {
                customerAccessService.assertCustomerAccess(request.customerId());
                customerAccessService.assertBookingAccess(request.bookingId());
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.bookingId()));
                if (!booking.getCustomer().getId().equals(customer.getId())) {
                        throw new org.springframework.security.access.AccessDeniedException("The booking does not belong to this customer");
                }

        List<OrderItem> items = new ArrayList<>();
        for (OrderDto.OrderItemRequest itemRequest : request.items()) {
            FoodItem foodItem = foodItemRepository.findById(itemRequest.foodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + itemRequest.foodItemId()));

            OrderItem item = new OrderItem();
            item.setFoodItem(foodItem);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(foodItem.getPrice());
            item.setLineTotal(foodItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
            items.add(item);
        }

        Order createdOrder = orderService.createOrder(customer.getId(), booking.getId(), items);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderDto.Response.fromEntity(createdOrder));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto.Response> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderDto.StatusUpdateRequest request
    ) {
        Order updatedOrder = orderService.updateOrderStatus(id, request.status());
        return ResponseEntity.ok(OrderDto.Response.fromEntity(updatedOrder));
    }
}
