package com.example.sportiva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private BookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();

    public MyBookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewBookings);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BookingAdapter(bookingList);
        recyclerView.setAdapter(adapter);

        loadBookings();
    }

    private void loadBookings() {
        showLoading(true);

        ApiService apiService = ApiClient.getSessionClient().create(ApiService.class);
        Call<MyBookingsResponse> call = apiService.getMyBookings();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MyBookingsResponse> call, Response<MyBookingsResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    MyBookingsResponse bookingsResponse = response.body();

                    if (bookingsResponse.getStatus() == 200 && bookingsResponse.getResponse() != null) {
                        List<Booking> bookings = bookingsResponse.getResponse().getResults();

                        if (bookings != null && !bookings.isEmpty()) {
                            showBookings(bookings);
                        } else {
                            showError("No bookings yet.");
                            bookingList.clear();
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        showError(bookingsResponse.getMessage() != null ? bookingsResponse.getMessage() : "An error occurred.");
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();

                            JSONObject jsonObject = new JSONObject(errorJson);
                            String message = jsonObject.optString("message", "Failed to load booking.");
                            showError(message);
                        } else {
                            showError("Failed to load booking.");
                        }
                    } catch (Exception e) {
                        showError("Failed: Unknown error.");
                    }
                }
            }

            @Override
            public void onFailure(Call<MyBookingsResponse> call, Throwable t) {
                showLoading(false);
                showError("Error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error."));
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void showBookings(List<Booking> bookings) {
        bookingList.clear();
        bookingList.addAll(bookings);
        adapter.notifyDataSetChanged();
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}