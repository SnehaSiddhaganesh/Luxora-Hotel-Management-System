package com.luxora.hotel.model;

public interface Payment {
    boolean process(double amount);
    PaymentType getPaymentType();
    String getTransactionReference();
}
