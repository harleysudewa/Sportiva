package com.example.sportiva;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSlotFragment extends Fragment {

    private EditText dateInput, timeInput, durationInput;
    private Spinner courtSpinner;
    private Button bookButton;
    private final Map<String, Integer> courtMap = new HashMap<>();

    public BookSlotFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_slot, container, false);

        courtSpinner = view.findViewById(R.id.courtSpinner);
        dateInput = view.findViewById(R.id.bookingDateInput);
        timeInput = view.findViewById(R.id.bookingTimeInput);
        durationInput = view.findViewById(R.id.bookingDurationInput);
        bookButton = view.findViewById(R.id.bookNowButton);

        dateInput.setFocusable(false);
        timeInput.setFocusable(false);

        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        String formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        dateInput.setText(formatted);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            Calendar minDate = Calendar.getInstance();
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 7);

            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
            datePickerDialog.show();
        });

        timeInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(
                    requireContext(),
                    (view12, hourOfDay, minute) -> {
                        String formatted = String.format("%02d:00", hourOfDay);
                        timeInput.setText(formatted);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    0, // minute default = 0
                    true
            ).show();
        });

        loadCourtsFromApi();

        bookButton.setOnClickListener(v -> {
            String selectedLabel = (String) courtSpinner.getSelectedItem();
            if (selectedLabel == null || !courtMap.containsKey(selectedLabel)) {
                Toast.makeText(getContext(), "Please select a court first.", Toast.LENGTH_SHORT).show();
                return;
            }

            int courtId = courtMap.get(selectedLabel);
            String bookingDate = dateInput.getText().toString();
            String bookingTime = timeInput.getText().toString();
            String durationStr = durationInput.getText().toString();

            if (bookingDate.isEmpty() || bookingTime.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration;
            try {
                duration = Integer.parseInt(durationStr);
                if (duration % 60 != 0) {
                    Toast.makeText(getContext(), "Duration must be in multiples of 60 minutes.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid duration.", Toast.LENGTH_SHORT).show();
                return;
            }

            BookingRequest request = new BookingRequest(courtId, bookingDate, bookingTime, duration);
            ApiService apiService = ApiClient.getSessionClient().create(ApiService.class);
            apiService.bookSlot(request).enqueue(new Callback<BookingResponse>() {
                @Override
                public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        BookingResponse res = response.body();
                        Intent intent = new Intent(getContext(), PaymentActivity.class);
                        intent.putExtra("bookingId", res.getResponse().bookingId);
                        intent.putExtra("payment_amount", res.getResponse().payment_amount);
                        intent.putExtra("court_name", selectedLabel);
                        intent.putExtra("duration", duration + " minutes");
                        intent.putExtra("date", bookingDate);
                        intent.putExtra("time", bookingTime + " - " + calculateEndTime(bookingTime, duration));
                        intent.putExtra("total", String.valueOf(res.getResponse().payment_amount));
                        startActivity(intent);
                    } else {
                        try {
                            String errorJson = response.errorBody().string();
                            JSONObject errorObj = new JSONObject(errorJson);
                            String message = errorObj.getString("message");

                            Toast.makeText(getContext(), "Booking failed: " + message, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Booking failed: Unknown error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private String calculateEndTime(String startTime, int durationMinutes) {
        try {
            String[] parts = startTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            int totalMinutes = hour * 60 + minute + durationMinutes;
            int endHour = totalMinutes / 60;
            int endMinute = totalMinutes % 60;

            return String.format("%02d:%02d", endHour, endMinute);
        } catch (Exception e) {
            return "";
        }
    }

    private void loadCourtsFromApi() {
        ApiService apiService = ApiClient.getSessionClient().create(ApiService.class);
        apiService.getAvailability().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AvailabilityResponse> call, @NonNull Response<AvailabilityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AvailabilityResponse.AvailabilityItem> items = response.body().response.availability;
                    Set<String> seen = new HashSet<>();
                    List<String> labels = new ArrayList<>();

                    for (AvailabilityResponse.AvailabilityItem item : items) {
                        String label = item.court_type + " (Court ID: " + item.court_id + ")";
                        if (!seen.contains(label)) {
                            seen.add(label);
                            labels.add(label);
                            courtMap.put(label, item.court_id);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, labels);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    courtSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AvailabilityResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load court data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}