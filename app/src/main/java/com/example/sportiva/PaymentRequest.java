package com.example.sportiva;

public class PaymentRequest {
    private int booking_id;
    private String payment_status;
    private int amount;
    private String payment_method;

    public PaymentRequest(int booking_id, String payment_status, int amount, String payment_method) {
        this.booking_id = booking_id;
        this.payment_status = payment_status;
        this.amount = amount;
        this.payment_method = payment_method;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public int getAmount() {
        return amount;
    }

    public String getPayment_method() {
        return payment_method;
    }
}