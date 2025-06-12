package com.example.sportiva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView welcomeText;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        welcomeText = view.findViewById(R.id.homeWelcomeText);
        fetchUsername();
        return view;
    }

    private void fetchUsername() {
        ApiService apiService = ApiClient.getSessionClient().create(ApiService.class);
        apiService.getProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().response != null) {
                    String username = response.body().response.username;
                    welcomeText.setText("Welcome, " + username + "!");
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                // keep default text
            }
        });
    }
}