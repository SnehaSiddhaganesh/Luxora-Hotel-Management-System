package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.OrderItem;
import com.luxora.hotel.model.OrderStatus;
import com.luxora.hotel.repository.BookingRepository;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.repository.OrderItemRepository;
import com.luxora.hotel.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           CustomerRepository customerRepository,
                           BookingRepository bookingRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Long customerId, Long bookingId, List<OrderItem> items) {
        if (customerId == null || bookingId == null) {
            throw new IllegalArgumentException("Customer and booking are required.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        Order order = new Order();
        order.setCustomer(customer);
        order.setBooking(booking);
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus(OrderStatus.PLACED);
        order.setOrderedAt(LocalDateTime.now());

        BigDecimal subtotal = calculateSubtotal(items);
        BigDecimal taxRate = BigDecimal.valueOf(10);
        BigDecimal tax = calculateTax(subtotal, taxRate);
        BigDecimal total = calculateTotal(subtotal, taxRate);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : items) {
            if (item == null || item.getFoodItem() == null) {
                throw new IllegalArgumentException("Each order item must reference a food item.");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Order item quantity must be greater than zero.");
            }
            FoodItem foodItem = item.getFoodItem();
            if (foodItem.getPrice() == null) {
                throw new IllegalArgumentException("Food item price is required.");
            }
            item.setOrder(savedOrder);
            item.setUnitPrice(foodItem.getPrice());
            item.setLineTotal(foodItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            savedOrder.getItems().add(orderItemRepository.save(item));
        }

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));
        return orderRepository.findByCustomerOrderByOrderedAtDesc(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersForBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));
        return orderRepository.findByBookingOrderByOrderedAtDesc(booking);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateSubtotal(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getLineTotal() != null ? item.getLineTotal() : item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRatePercent) {
        if (subtotal == null) {
            return BigDecimal.ZERO;
        }
        if (taxRatePercent == null) {
            taxRatePercent = BigDecimal.valueOf(10);
        }
        BigDecimal percent = taxRatePercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return subtotal.multiply(percent);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal taxRatePercent) {
        return subtotal.add(calculateTax(subtotal, taxRatePercent));
    }
}
