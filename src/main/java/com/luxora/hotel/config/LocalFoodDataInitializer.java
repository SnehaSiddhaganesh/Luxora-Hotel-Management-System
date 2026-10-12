package com.luxora.hotel.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.model.DietaryType;
import com.luxora.hotel.model.FoodCategory;
import com.luxora.hotel.repository.FoodItemRepository;

@Component
@Profile("local")
public class LocalFoodDataInitializer implements ApplicationRunner {

    private final FoodItemRepository foodItemRepository;

    public LocalFoodDataInitializer(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (foodItemRepository.count() > 0) {
            return;
        }

        foodItemRepository.saveAll(List.of(
                food("food-101", "Masala Dosa", 180, FoodCategory.BREAKFAST),
                food("food-102", "Idli (3 Pcs)", 140, FoodCategory.BREAKFAST),
                food("food-103", "Indori Poha", 120, FoodCategory.BREAKFAST),
                food("food-104", "Artisan Veg Sandwich", 160, FoodCategory.BREAKFAST),
                food("food-201", "Paneer Butter Masala", 340, FoodCategory.MAIN_COURSE),
                food("food-202", "Royal Veg Biryani", 320, FoodCategory.MAIN_COURSE),
                food("food-203", "Dal Tadka (Desi Ghee)", 240, FoodCategory.MAIN_COURSE),
                food("food-204", "Butter Roti (2 Pcs)", 40, FoodCategory.MAIN_COURSE),
                food("food-301", "Charbroiled Paneer Tikka", 280, FoodCategory.SNACKS),
                food("food-302", "Chilli Paneer (Dry/Gravy)", 290, FoodCategory.SNACKS),
                food("food-303", "Wok Schezwan Noodles", 260, FoodCategory.SNACKS),
                food("food-401", "Fiery Chef Spicy Biryani", 360, FoodCategory.SPECIALTY),
                food("food-501", "Warm Gulab Jamun (2 Pcs)", 150, FoodCategory.SWEETS),
                food("food-502", "Saffron Rasmalai (2 Pcs)", 180, FoodCategory.SWEETS),
                food("food-503", "Belgian Chocolate Cake", 220, FoodCategory.SWEETS),
                food("food-504", "Artisan Gourmet Ice Cream", 160, FoodCategory.SWEETS),
                food("food-601", "Filter Coffee / Cappuccino", 120, FoodCategory.BEVERAGES),
                food("food-602", "Masala Ginger Tea", 90, FoodCategory.BEVERAGES),
                food("food-603", "Cold-Pressed Fresh Juice", 150, FoodCategory.BEVERAGES),
                food("food-604", "Belgian Chocolate Milkshake", 180, FoodCategory.BEVERAGES),
                food("food-701", "Royal Continental Platter", 650, FoodCategory.BREAKFAST),
                food("food-702", "Gourmet Ribeye Steak", 1450, FoodCategory.MAIN_COURSE),
                food("food-703", "Flame Tandoori Tikka", 750, FoodCategory.SPECIALTY),
                food("food-704", "Gold Leaf Chocolate Soufflé", 850, FoodCategory.SWEETS)
        ));
    }

    private static FoodItem food(String code, String name, int price, FoodCategory category) {
        return new FoodItem(code, name, BigDecimal.valueOf(price), category, DietaryType.VEGETARIAN, true);
    }
}