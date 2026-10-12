package com.luxora.hotel.model;

public class SnackItem extends FoodItem {
    public SnackItem(String itemId, String name, double price, boolean available) {
        this(itemId, name, price, DietaryType.GENERIC, available);
    }

    public SnackItem(String itemId, String name, double price, DietaryType dietaryType, boolean available) {
        super(itemId, name, price, FoodCategory.SNACKS, dietaryType, available);
    }

    @Override
    public String getCuisineType() {
        return "Light Gourmet";
    }

    @Override
    public String getPreparationStyle() {
        return "Quick service snack platter";
    }

    @Override
    public double calculatePrice(int quantity) {
        return super.calculatePrice(quantity) + (quantity > 3 ? 15.0 : 0.0);
    }

    @Override
    public String getServingSuggestion() {
        return "Perfect as a quick in-room bite or evening tea companion.";
    }
}
