package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.entity.OrderItem;
import com.luxora.hotel.exception.FoodItemNotFoundException;
import com.luxora.hotel.exception.InvalidQuantityException;

@Service
public class CartService {
    private final Map<Long, OrderItem> cartItems = new LinkedHashMap<>();

    public void addItem(FoodItem item, int quantity) {
        if (item == null) {
            throw new FoodItemNotFoundException("Food item was not found.");
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero.");
        }
        if (!item.isAvailable()) {
            throw new FoodItemNotFoundException("Food item is unavailable: " + item.getName());
        }

        if (cartItems.containsKey(item.getId())) {
            OrderItem existing = cartItems.get(item.getId());
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setLineTotal(existing.getUnitPrice().multiply(BigDecimal.valueOf(existing.getQuantity())));
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setFoodItem(item);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(item.getPrice());
            newItem.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItems.put(item.getId(), newItem);
        }
    }

    public void removeItem(Long itemId) {
        if (itemId == null) {
            throw new FoodItemNotFoundException("Item ID is required.");
        }
        if (!cartItems.containsKey(itemId)) {
            throw new FoodItemNotFoundException("Food item not found in cart: " + itemId);
        }
        cartItems.remove(itemId);
    }

    public void updateQuantity(Long itemId, int quantity) {
        if (!cartItems.containsKey(itemId)) {
            throw new FoodItemNotFoundException("Food item not found in cart: " + itemId);
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero.");
        }

        OrderItem item = cartItems.get(itemId);
        item.setQuantity(quantity);
        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(cartItems.values());
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public BigDecimal calculateSubtotal() {
        return cartItems.values().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTax(BigDecimal taxRatePercent) {
        if (taxRatePercent.compareTo(BigDecimal.ZERO) < 0 || taxRatePercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 100.");
        }
        BigDecimal percent = taxRatePercent.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
        return calculateSubtotal().multiply(percent);
    }

    public BigDecimal calculateTotal(BigDecimal taxRatePercent) {
        return calculateSubtotal().add(calculateTax(taxRatePercent));
    }

    public void clear() {
        cartItems.clear();
    }
}
