package com.luxora.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.model.FoodCategory;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    Optional<FoodItem> findByItemCode(String itemCode);
    List<FoodItem> findByCategory(FoodCategory category);
    List<FoodItem> findByCategoryOrderByNameAsc(FoodCategory category);
    List<FoodItem> findByAvailableTrue();
    List<FoodItem> findByAvailableTrueOrderByNameAsc();
    List<FoodItem> findByAvailableTrueAndCategory(FoodCategory category);
    List<FoodItem> findByNameContainingIgnoreCase(String name);
}