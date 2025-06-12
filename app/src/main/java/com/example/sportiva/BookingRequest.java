package com.example.sportiva;

public class BookingRequest {
    private int court_id;
    private String booking_date;
    private String booking_time;
    private int booking_duration;

    public BookingRequest(int court_id, String booking_date, String booking_time, int booking_duration) {
        this.court_id = court_id;
        this.booking_date = booking_date;
        this.booking_time = booking_time;
        this.booking_duration = booking_duration;
    }
}
