package com.luxora.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByOrderOrderByIdAsc(Order order);
    List<OrderItem> findByFoodItem(FoodItem foodItem);
}