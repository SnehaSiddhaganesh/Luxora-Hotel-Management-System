package com.luxora.hotel.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.Order;
import com.luxora.hotel.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);
    @Override
    @EntityGraph(attributePaths = {"items", "items.foodItem", "booking", "booking.room"})
    Optional<Order> findById(Long id);
    List<Order> findByCustomer(Customer customer);
    @EntityGraph(attributePaths = {"items", "items.foodItem", "booking", "booking.room"})
    List<Order> findByCustomerOrderByOrderedAtDesc(Customer customer);
    List<Order> findByCustomerAndStatus(Customer customer, OrderStatus status);
    List<Order> findByBooking(Booking booking);
    @EntityGraph(attributePaths = {"items", "items.foodItem", "booking", "booking.room"})
    List<Order> findByBookingOrderByOrderedAtDesc(Booking booking);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByStatusIn(Collection<OrderStatus> statuses);
}