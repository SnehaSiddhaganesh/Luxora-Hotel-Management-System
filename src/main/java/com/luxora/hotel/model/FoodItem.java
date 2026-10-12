package com.luxora.hotel.model;

public abstract class FoodItem implements MenuItem {
    private final String itemId;
    private String name;
    private double price;
    private FoodCategory category;
    private DietaryType dietaryType;
    private boolean available;

    protected FoodItem(String itemId, String name, double price, FoodCategory category, DietaryType dietaryType, boolean available) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("Food item id cannot be blank.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Food item name is required.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Food price must be positive.");
        }

        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category != null ? category : FoodCategory.SPECIALTY;
        this.dietaryType = dietaryType != null ? dietaryType : DietaryType.GENERIC;
        this.available = available;
    }

    protected FoodItem(String itemId, String name, double price, FoodCategory category, boolean available) {
        this(itemId, name, price, category, DietaryType.GENERIC, available);
    }

    public abstract String getCuisineType();

    public abstract String getPreparationStyle();

    @Override
    public double calculatePrice(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        return price * quantity;
    }

    public double getTotalPrice(int quantity) {
        return calculatePrice(quantity);
    }

    public double calculatePrice() {
        return price;
    }

    public double calculatePrice(int quantity, double discountPercent) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }

        double subtotal = calculatePrice(quantity);
        return subtotal - (subtotal * discountPercent / 100);
    }

    public String getServingSuggestion() {
        return "Freshly prepared and served with care.";
    }

    public boolean isSuitableFor(DietaryType type) {
        if (type == null) {
            return true;
        }
        return dietaryType == type || dietaryType == DietaryType.GENERIC;
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price > 0) {
            this.price = price;
        }
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category != null ? category : FoodCategory.SPECIALTY;
    }

    public DietaryType getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(DietaryType dietaryType) {
        this.dietaryType = dietaryType != null ? dietaryType : DietaryType.GENERIC;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return name + " (" + category.getDisplayName() + ", " + dietaryType + ")";
    }
}
