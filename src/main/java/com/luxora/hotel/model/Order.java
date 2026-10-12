package com.luxora.hotel.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String orderId;
    private final Customer customer;
    private final Booking booking;
    private final List<OrderItem> items = new ArrayList<>();
    private LocalDateTime orderTime;
    private OrderStatus status;

    public Order(String orderId, Customer customer, Booking booking) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order id is required.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required.");
        }
        if (booking == null) {
            throw new IllegalArgumentException("Booking is required.");
        }

        this.orderId = orderId;
        this.customer = customer;
        this.booking = booking;
        this.orderTime = LocalDateTime.now();
        this.status = OrderStatus.PLACED;
    }

    public void addItem(OrderItem item) {
        if (item != null) {
            items.add(item);
        }
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Booking getBooking() {
        return booking;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
