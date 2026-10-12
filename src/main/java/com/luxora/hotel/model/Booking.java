package com.luxora.hotel.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private final String bookingId;
    private final Customer customer;
    private final Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private final List<Order> orders = new ArrayList<>();

    public Booking(String bookingId, Customer customer, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        if (bookingId == null || bookingId.isBlank()) {
            throw new IllegalArgumentException("Booking id is required.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required.");
        }
        if (room == null) {
            throw new IllegalArgumentException("Room is required.");
        }
        if (checkInDate == null || checkOutDate == null || !checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        this.bookingId = bookingId;
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = BookingStatus.PENDING;
    }

    public long getNights() {
        return java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    public double getTotalAmount() {
        return room.getPricePerNight() * getNights();
    }

    public void addOrder(Order order) {
        if (order != null) {
            orders.add(order);
        }
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }
}
