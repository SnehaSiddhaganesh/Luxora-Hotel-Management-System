package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.OrderItem;
import com.luxora.hotel.exception.InvalidBookingException;
import com.luxora.hotel.exception.InvalidQuantityException;
import com.luxora.hotel.model.OrderStatus;

public class FoodOrderService {
    public Order placeOrder(Customer customer, Booking booking, CartService cart, BigDecimal taxRatePercent) {
        if (customer == null) {
            throw new InvalidBookingException("Customer is required for placing a food order.");
        }
        if (booking == null) {
            throw new InvalidBookingException("Booking is required for placing a food order.");
        }
        if (cart == null || cart.isEmpty()) {
            throw new InvalidQuantityException("Cart must contain at least one item.");
        }
        if (taxRatePercent == null || taxRatePercent.compareTo(BigDecimal.ZERO) < 0 || taxRatePercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 100.");
        }

        List<OrderItem> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new InvalidQuantityException("Cart must contain at least one item.");
        }

        BigDecimal subtotal = cart.calculateSubtotal();
        BigDecimal tax = cart.calculateTax(taxRatePercent);
        BigDecimal total = subtotal.add(tax);

        Order order = new Order();
        order.setCustomer(customer);
        order.setBooking(booking);
        order.setStatus(OrderStatus.PLACED);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(total);

        return order;
    }

    public BigDecimal calculateTax(CartService cart, BigDecimal taxRatePercent) {
        if (cart == null) {
            throw new InvalidBookingException("Cart is required to calculate taxes.");
        }
        if (taxRatePercent == null) {
            throw new IllegalArgumentException("Tax rate is required.");
        }
        BigDecimal percent = taxRatePercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return cart.calculateSubtotal().multiply(percent);
    }
}
