package com.example.sportiva;

public class BookingResponse {
    private String message;
    private int status;
    private Object error;
    private ResponseData response;

    public static class ResponseData {
        public int bookingId;
        public int payment_amount;
    }

    public String getMessage() {
        return message;
    }

    public ResponseData getResponse() {
        return response;
    }
}
