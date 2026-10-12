package com.luxora.hotel.model;

public class UPIPayment implements Payment {
    private final String upiId;
    private final String transactionReference;

    public UPIPayment(String upiId, String transactionReference) {
        if (upiId == null || upiId.isBlank()) {
            throw new IllegalArgumentException("UPI ID is required.");
        }
        if (transactionReference == null || transactionReference.isBlank()) {
            throw new IllegalArgumentException("Transaction reference is required.");
        }
        this.upiId = upiId;
        this.transactionReference = transactionReference;
    }

    @Override
    public boolean process(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("UPI payment amount must be positive.");
        }
        System.out.println("UPI payment processed for amount: ₹" + amount + " to " + upiId);
        return true;
    }

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.UPI;
    }

    @Override
    public String getTransactionReference() {
        return transactionReference;
    }
}
