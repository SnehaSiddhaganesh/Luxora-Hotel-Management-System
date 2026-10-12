package com.luxora.hotel.exception;

public class FoodItemNotFoundException extends RuntimeException {
    public FoodItemNotFoundException(String message) {
        super(message);
    }
}
