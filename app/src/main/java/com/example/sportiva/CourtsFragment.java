package com.example.sportiva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourtsFragment extends Fragment {

    private Spinner courtSpinner, dateSpinner;
    private RecyclerView timeSlotsRecyclerView;

    private Map<String, AvailabilityResponse.AvailabilityItem> itemMap = new HashMap<>();
    private Map<Integer, Set<String>> courtDatesMap = new HashMap<>();
    private Map<Integer, String> courtLabels = new HashMap<>();
    private List<String> courtOptions = new ArrayList<>();
    private Map<String, Integer> courtLabelToId = new HashMap<>();

    public CourtsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courts, container, false);

        courtSpinner = view.findViewById(R.id.courtSpinner);
        dateSpinner = view.findViewById(R.id.dateSpinner);
        timeSlotsRecyclerView = view.findViewById(R.id.timeSlotsRecyclerView);
        timeSlotsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchAvailability();
        return view;
    }

    private void fetchAvailability() {
        ApiService api = ApiClient.getSessionClient().create(ApiService.class);
        api.getAvailability().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AvailabilityResponse.AvailabilityItem> items = response.body().response.availability;
                    for (AvailabilityResponse.AvailabilityItem item : items) {
                        String key = item.court_id + "_" + item.date;
                        itemMap.put(key, item);

                        courtLabels.put(item.court_id, item.court_type);

                        if (!courtDatesMap.containsKey(item.court_id)) {
                            courtDatesMap.put(item.court_id, new HashSet<String>());
                        }
                        courtDatesMap.get(item.court_id).add(item.date);
                    }

                    courtOptions.clear();
                    for (Map.Entry<Integer, String> entry : courtLabels.entrySet()) {
                        String label = entry.getValue() + " (Court ID: " + entry.getKey() + ")";
                        courtOptions.add(label);
                        courtLabelToId.put(label, entry.getKey());
                    }

                    updateCourtSpinner();
                }
            }

            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load availability.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCourtSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, courtOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courtSpinner.setAdapter(adapter);

        courtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLabel = courtOptions.get(position);
                int courtId = courtLabelToId.get(selectedLabel);
                Set<String> dateSet = courtDatesMap.containsKey(courtId) ? courtDatesMap.get(courtId) : new HashSet<>();
                List<String> dates = new ArrayList<>(dateSet);
                Collections.sort(dates);
                updateDateSpinner(courtId, dates);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateDateSpinner(final int courtId, final List<String> dates) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = dates.get(position);
                String key = courtId + "_" + selectedDate;
                AvailabilityResponse.AvailabilityItem item = itemMap.get(key);
                if (item != null) {
                    showAvailableTimes(item);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showAvailableTimes(AvailabilityResponse.AvailabilityItem item) {
        List<String> allTimes = new ArrayList<>();
        int openHour = Integer.parseInt(item.open_time.substring(0, 2));
        int closeHour = Integer.parseInt(item.close_time.substring(0, 2));

        for (int hour = openHour; hour < closeHour; hour++) {
            allTimes.add(String.format("%02d:00", hour));
        }

        TimeSlotAdapter adapter = new TimeSlotAdapter(allTimes, item.available_times);
        timeSlotsRecyclerView.setAdapter(adapter);
    }
}
