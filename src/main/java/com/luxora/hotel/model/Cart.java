package com.luxora.hotel.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        if (item != null) {
            items.add(item);
        }
    }

    public void addItem(FoodItem foodItem) {
        addItem(foodItem, 1);
    }

    public void addItem(FoodItem foodItem, int quantity) {
        if (foodItem == null) {
            throw new IllegalArgumentException("Food item cannot be null.");
        }
        if (!foodItem.isAvailable()) {
            throw new IllegalStateException("Food item is currently unavailable.");
        }
        items.add(new OrderItem(foodItem, quantity));
    }

    public void removeItem(String foodItemId) {
        items.removeIf(item -> item.getFoodItem().getItemId().equals(foodItemId));
    }

    public void removeItem(FoodItem foodItem) {
        if (foodItem != null) {
            items.removeIf(item -> item.getFoodItem().getItemId().equals(foodItem.getItemId()));
        }
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public void clear() {
        items.clear();
    }
}
