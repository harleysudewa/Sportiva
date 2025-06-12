package com.example.sportiva;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/logout")
    Call<GenericResponse> logout();

    @POST("login_customer")
    Call<JsonObject> loginCustomer(@Body JsonObject body);

    @POST("/register_customer")
    Call<JsonObject> registerCustomer(@Body JsonObject body);

    @POST("/send_reset_password")
    Call<JsonObject> sendResetPassword(@Body JsonObject body);

    @GET("/my_bookings")
    Call<MyBookingsResponse> getMyBookings();

    @POST("/book_slot")
    Call<BookingResponse> bookSlot(@Body BookingRequest bookingRequest);

    @GET("/availability")
    Call<AvailabilityResponse> getAvailability();

    @POST("/payment")
    Call<ResponseBody> confirmPayment(@Body PaymentRequest paymentRequest);

    @GET("/profile")
    Call<ProfileResponse> getProfile();
}
