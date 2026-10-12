package com.luxora.hotel.model;

public class OrderItem {
    private final FoodItem foodItem;
    private int quantity;

    public OrderItem(FoodItem foodItem, int quantity) {
        if (foodItem == null) {
            throw new IllegalArgumentException("Food item is required.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return foodItem.getTotalPrice(quantity);
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }
}
