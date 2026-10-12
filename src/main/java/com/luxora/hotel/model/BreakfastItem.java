package com.luxora.hotel.model;

public class BreakfastItem extends FoodItem {
    public BreakfastItem(String itemId, String name, double price, boolean available) {
        this(itemId, name, price, DietaryType.GENERIC, available);
    }

    public BreakfastItem(String itemId, String name, double price, DietaryType dietaryType, boolean available) {
        super(itemId, name, price, FoodCategory.BREAKFAST, dietaryType, available);
    }

    @Override
    public String getCuisineType() {
        return "Continental";
    }

    @Override
    public String getPreparationStyle() {
        return "Freshly prepared breakfast platter";
    }

    @Override
    public double calculatePrice(int quantity) {
        double basePrice = super.calculatePrice(quantity);
        return quantity >= 2 ? basePrice + 25.0 : basePrice;
    }

    @Override
    public String getServingSuggestion() {
        return "Served with seasonal fruit, coffee, and a warm bakery basket.";
    }
}
