package com.luxora.hotel.model;

public interface MenuItem {
    String getItemId();
    String getName();
    double getPrice();
    boolean isAvailable();
    double calculatePrice(int quantity);
    String getServingSuggestion();
}
