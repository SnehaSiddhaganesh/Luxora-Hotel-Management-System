package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.model.FoodCategory;
import com.luxora.hotel.repository.FoodItemRepository;

@Service
public class FoodItemServiceImpl implements FoodItemService {
    private final FoodItemRepository foodItemRepository;

    public FoodItemServiceImpl(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    @Override
    @Transactional
    public FoodItem saveFoodItem(FoodItem foodItem) {
        if (foodItem == null) {
            throw new IllegalArgumentException("Food item details are required.");
        }
        if (foodItem.getItemCode() == null || foodItem.getItemCode().isBlank()) {
            throw new IllegalArgumentException("Food item code is required.");
        }
        if (foodItem.getPrice() == null || foodItem.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Food item price must be greater than zero.");
        }
        return foodItemRepository.save(foodItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FoodItem> getFoodItemById(Long foodItemId) {
        return foodItemRepository.findById(foodItemId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FoodItem> getFoodItemByCode(String itemCode) {
        if (itemCode == null || itemCode.isBlank()) {
            return Optional.empty();
        }
        return foodItemRepository.findByItemCode(itemCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodItem> getAvailableFoodItems() {
        return foodItemRepository.findByAvailableTrueOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodItem> getFoodItemsByCategory(FoodCategory category) {
        if (category == null) {
            return foodItemRepository.findByAvailableTrueOrderByNameAsc();
        }
        return foodItemRepository.findByAvailableTrueAndCategory(category);
    }
}
