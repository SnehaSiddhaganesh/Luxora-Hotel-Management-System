package com.luxora.hotel.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    public Customer(String customerId, String firstName, String lastName, String email, String phone, String address) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer id is required.");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            bookings.add(booking);
        }
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    public void addOrder(Order order) {
        if (order != null) {
            orders.add(order);
        }
    }

    public double getTotalSpend() {
        return orders.stream().mapToDouble(Order::getTotalAmount).sum();
    }
}
