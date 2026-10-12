package com.luxora.hotel.model;

public class CashPayment implements Payment {
    private final String transactionReference;

    public CashPayment(String transactionReference) {
        if (transactionReference == null || transactionReference.isBlank()) {
            throw new IllegalArgumentException("Cash transaction reference is required.");
        }
        this.transactionReference = transactionReference;
    }

    @Override
    public boolean process(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Cash payment amount must be positive.");
        }
        System.out.println("Cash payment processed for amount: ₹" + amount);
        return true;
    }

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.CASH;
    }

    @Override
    public String getTransactionReference() {
        return transactionReference;
    }
}
