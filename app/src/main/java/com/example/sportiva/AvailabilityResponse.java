package com.example.sportiva;

import java.util.List;

public class AvailabilityResponse {
    public String message;
    public int status;
    public Object error;
    public AvailabilityData response;

    public static class AvailabilityData {
        public List<AvailabilityItem> availability;
    }

    public static class AvailabilityItem {
        public int court_id;
        public String court_type;
        public String date;
        public String open_time;
        public String close_time;
        public List<String> available_times;
    }
}
