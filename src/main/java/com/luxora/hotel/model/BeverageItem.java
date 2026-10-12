package com.luxora.hotel.model;

public class BeverageItem extends FoodItem {
    public BeverageItem(String itemId, String name, double price, boolean available) {
        this(itemId, name, price, DietaryType.GENERIC, available);
    }

    public BeverageItem(String itemId, String name, double price, DietaryType dietaryType, boolean available) {
        super(itemId, name, price, FoodCategory.BEVERAGES, dietaryType, available);
    }

    @Override
    public String getCuisineType() {
        return "Refreshment";
    }

    @Override
    public String getPreparationStyle() {
        return "Freshly brewed and chilled beverage service";
    }

    @Override
    public double calculatePrice(int quantity, double discountPercent) {
        double subtotal = super.calculatePrice(quantity);
        double extraChillFee = quantity >= 2 ? 10.0 : 0.0;
        return subtotal - (subtotal * discountPercent / 100) + extraChillFee;
    }

    @Override
    public String getServingSuggestion() {
        return "Served chilled, with optional citrus garnish or artisan foam.";
    }
}
