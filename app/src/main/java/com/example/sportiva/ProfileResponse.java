package com.example.sportiva;

public class ProfileResponse {
    public String message;
    public int status;
    public String error;
    public User response;

    public static class User {
        public int id;
        public String username;
        public String role;
    }
}
