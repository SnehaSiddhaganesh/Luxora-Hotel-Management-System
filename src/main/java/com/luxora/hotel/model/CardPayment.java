package com.luxora.hotel.model;

public class CardPayment implements Payment {
    private final String cardNumber;
    private final String transactionReference;

    public CardPayment(String cardNumber, String transactionReference) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Card number is required.");
        }
        if (transactionReference == null || transactionReference.isBlank()) {
            throw new IllegalArgumentException("Transaction reference is required.");
        }
        this.cardNumber = cardNumber;
        this.transactionReference = transactionReference;
    }

    @Override
    public boolean process(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Card payment amount must be positive.");
        }
        System.out.println("Card payment processed for amount: ₹" + amount + " using card ending " + cardNumber.substring(Math.max(0, cardNumber.length() - 4)));
        return true;
    }

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.CARD;
    }

    @Override
    public String getTransactionReference() {
        return transactionReference;
    }
}
