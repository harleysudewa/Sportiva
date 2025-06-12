package com.example.sportiva;

import com.google.gson.annotations.SerializedName;

public class Booking {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("court_id")
    private int courtId;

    @SerializedName("booking_date")
    private String bookingDate;

    @SerializedName("booking_time")
    private String bookingTime;

    @SerializedName("booking_duration")
    private int bookingDuration;

    @SerializedName("status")
    private String status;

    @SerializedName("payment_amount")
    private int paymentAmount;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("court_type")
    private String courtType;

    // Getters
    public int getId() { return id; }
    public String getBookingDate() { return bookingDate; }
    public String getBookingTime() { return bookingTime; }
    public int getBookingDuration() { return bookingDuration; }
    public String getStatus() { return status; }
    public int getPaymentAmount() { return paymentAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCreatedAt() { return createdAt; }
    public String getCourtType() { return courtType; }
}