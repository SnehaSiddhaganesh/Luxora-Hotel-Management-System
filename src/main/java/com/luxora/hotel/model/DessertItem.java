package com.luxora.hotel.model;

public class DessertItem extends FoodItem {
    public DessertItem(String itemId, String name, double price, boolean available) {
        this(itemId, name, price, DietaryType.GENERIC, available);
    }

    public DessertItem(String itemId, String name, double price, DietaryType dietaryType, boolean available) {
        super(itemId, name, price, FoodCategory.SWEETS, dietaryType, available);
    }

    @Override
    public String getCuisineType() {
        return "Dessert";
    }

    @Override
    public String getPreparationStyle() {
        return "Artisan dessert and finishing touches";
    }

    @Override
    public String getServingSuggestion() {
        return "Best served chilled or warm with a handcrafted garnish.";
    }
}
