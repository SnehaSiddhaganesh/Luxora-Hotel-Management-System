package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.OrderItem;
import com.luxora.hotel.model.OrderStatus;

public interface OrderService {
    Order createOrder(Long customerId, Long bookingId, List<OrderItem> items);
    Optional<Order> getOrderById(Long orderId);
    List<Order> getOrdersForCustomer(Long customerId);
    List<Order> getOrdersForBooking(Long bookingId);
    Order updateOrderStatus(Long orderId, OrderStatus status);
    BigDecimal calculateSubtotal(List<OrderItem> items);
    BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRatePercent);
    BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal taxRatePercent);
}
