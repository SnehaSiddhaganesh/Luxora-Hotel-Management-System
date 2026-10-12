package com.luxora.hotel.dto;

import java.math.BigDecimal;

import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.model.DietaryType;
import com.luxora.hotel.model.FoodCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FoodItemDto {

    public record CreateRequest(
            @NotBlank(message = "Item code is required") String itemCode,
            @NotBlank(message = "Item name is required") String name,
            @NotNull(message = "Price is required") @Positive(message = "Price must be greater than zero") BigDecimal price,
            @NotNull(message = "Category is required") FoodCategory category,
            @NotNull(message = "Dietary type is required") DietaryType dietaryType,
            boolean available
    ) {
    }

    public record UpdateRequest(
            @NotBlank(message = "Item code is required") String itemCode,
            @NotBlank(message = "Item name is required") String name,
            @NotNull(message = "Price is required") @Positive(message = "Price must be greater than zero") BigDecimal price,
            @NotNull(message = "Category is required") FoodCategory category,
            @NotNull(message = "Dietary type is required") DietaryType dietaryType,
            boolean available
    ) {
    }

    public record Response(
            Long id,
            String itemCode,
            String name,
            BigDecimal price,
            FoodCategory category,
            DietaryType dietaryType,
            boolean available
    ) {
        public static Response fromEntity(FoodItem foodItem) {
            if (foodItem == null) {
                return null;
            }

            return new Response(
                    foodItem.getId(),
                    foodItem.getItemCode(),
                    foodItem.getName(),
                    foodItem.getPrice(),
                    foodItem.getCategory(),
                    foodItem.getDietaryType(),
                    foodItem.isAvailable()
            );
        }
    }
}
