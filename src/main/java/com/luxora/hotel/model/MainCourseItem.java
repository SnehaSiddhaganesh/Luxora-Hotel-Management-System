package com.luxora.hotel.model;

public class MainCourseItem extends FoodItem {
    public MainCourseItem(String itemId, String name, double price, boolean available) {
        this(itemId, name, price, DietaryType.GENERIC, available);
    }

    public MainCourseItem(String itemId, String name, double price, DietaryType dietaryType, boolean available) {
        super(itemId, name, price, FoodCategory.MAIN_COURSE, dietaryType, available);
    }

    @Override
    public String getCuisineType() {
        return "World Fusion";
    }

    @Override
    public String getPreparationStyle() {
        return "Chef's signature plating with premium ingredients";
    }

    @Override
    public String getServingSuggestion() {
        return "Best served with premium sides and a chef's special sauce.";
    }
}
