package com.example.sportiva;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button logoutButton = view.findViewById(R.id.logoutButton);
        TextView userText = view.findViewById(R.id.userText);

        ApiService apiService = ApiClient.getSessionClient().create(ApiService.class);
        apiService.getProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().response != null) {
                    String username = response.body().response.username;
                    userText.setText("You are currently logged in as " + username + ".");
                } else {
                    userText.setText("Failed to load username.");
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                userText.setText("Error loading profile.");
            }
        });

        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (dialog, which) -> doLogout())
                    .show();
        });
    }

    private void doLogout() {
        ApiService apiService = ApiClient.getSessionClient().create(ApiService.class);
        apiService.logout().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    ApiClient.clearCookies();
                    Toast.makeText(getContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    try {
                        String errorJson = response.errorBody().string();
                        JSONObject errorObj = new JSONObject(errorJson);
                        String message = errorObj.getString("message");

                        Toast.makeText(getContext(), "Logout failed: " + message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Logout failed: Unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Logout error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}