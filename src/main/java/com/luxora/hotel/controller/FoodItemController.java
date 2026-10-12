package com.luxora.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luxora.hotel.dto.FoodItemDto;
import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.model.FoodCategory;
import com.luxora.hotel.repository.FoodItemRepository;
import com.luxora.hotel.service.FoodItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {

    private final FoodItemService foodItemService;
    private final FoodItemRepository foodItemRepository;

    public FoodItemController(FoodItemService foodItemService, FoodItemRepository foodItemRepository) {
        this.foodItemService = foodItemService;
        this.foodItemRepository = foodItemRepository;
    }

    @GetMapping
    public List<FoodItemDto.Response> getAllFoodItems(@RequestParam(required = false) FoodCategory category) {
        if (category != null) {
            return foodItemService.getFoodItemsByCategory(category)
                    .stream()
                    .map(FoodItemDto.Response::fromEntity)
                    .toList();
        }

        return foodItemService.getAllFoodItems().stream().map(FoodItemDto.Response::fromEntity).toList();
    }

    @GetMapping("/available")
    public List<FoodItemDto.Response> getAvailableFoodItems() {
        return foodItemService.getAvailableFoodItems().stream().map(FoodItemDto.Response::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public FoodItemDto.Response getFoodItemById(@PathVariable Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
        return FoodItemDto.Response.fromEntity(foodItem);
    }

    @PostMapping
    public ResponseEntity<FoodItemDto.Response> createFoodItem(@Valid @RequestBody FoodItemDto.CreateRequest request) {
        FoodItem foodItem = new FoodItem();
        foodItem.setItemCode(request.itemCode());
        foodItem.setName(request.name());
        foodItem.setPrice(request.price());
        foodItem.setCategory(request.category());
        foodItem.setDietaryType(request.dietaryType());
        foodItem.setAvailable(request.available());

        FoodItem savedFoodItem = foodItemService.saveFoodItem(foodItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(FoodItemDto.Response.fromEntity(savedFoodItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemDto.Response> updateFoodItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemDto.UpdateRequest request
    ) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));

        foodItem.setItemCode(request.itemCode());
        foodItem.setName(request.name());
        foodItem.setPrice(request.price());
        foodItem.setCategory(request.category());
        foodItem.setDietaryType(request.dietaryType());
        foodItem.setAvailable(request.available());

        FoodItem updatedFoodItem = foodItemRepository.save(foodItem);
        return ResponseEntity.ok(FoodItemDto.Response.fromEntity(updatedFoodItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Food item not found with id: " + id);
        }

        foodItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
