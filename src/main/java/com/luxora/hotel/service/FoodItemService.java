package com.luxora.hotel.service;

import java.util.List;
import java.util.Optional;

import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.model.FoodCategory;

public interface FoodItemService {
    FoodItem saveFoodItem(FoodItem foodItem);
    Optional<FoodItem> getFoodItemById(Long foodItemId);
    Optional<FoodItem> getFoodItemByCode(String itemCode);
    List<FoodItem> getAllFoodItems();
    List<FoodItem> getAvailableFoodItems();
    List<FoodItem> getFoodItemsByCategory(FoodCategory category);
}
