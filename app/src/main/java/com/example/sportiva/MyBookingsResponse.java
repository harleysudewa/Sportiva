package com.example.sportiva;

import com.google.gson.annotations.SerializedName;

public class MyBookingsResponse {
    @SerializedName("message")
    private String message;
    
    @SerializedName("status")
    private int status;
    
    @SerializedName("error")
    private String error;
    
    @SerializedName("response")
    private BookingResults response;
    
    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public BookingResults getResponse() { return response; }
    
    public static class BookingResults {
        @SerializedName("results")
        private java.util.List<Booking> results;
        
        public java.util.List<Booking> getResults() { return results; }
    }
}