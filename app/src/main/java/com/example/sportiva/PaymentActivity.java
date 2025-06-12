package com.example.sportiva;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    EditText bookingIdInput, paymentAmountInput;
    Spinner paymentMethodSpinner;
    Button payButton;

    private int bookingId;
    private int paymentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        bookingIdInput = findViewById(R.id.bookingIdInput);
        paymentAmountInput = findViewById(R.id.paymentAmountInput);
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);
        payButton = findViewById(R.id.payButton);
        TextView summaryCourt = findViewById(R.id.summaryCourt);
        TextView summaryDuration = findViewById(R.id.summaryDuration);
        TextView summaryDate = findViewById(R.id.summaryDate);
        TextView summaryTime = findViewById(R.id.summaryTime);
        TextView summaryTotal = findViewById(R.id.summaryTotal);

        bookingId = getIntent().getIntExtra("bookingId", -1);
        paymentAmount = getIntent().getIntExtra("payment_amount", 0);
        String courtName = getIntent().getStringExtra("court_name");
        String durationStr = getIntent().getStringExtra("duration");
        String bookingDate = getIntent().getStringExtra("date");
        String bookingTime = getIntent().getStringExtra("time");
        String totalAmount = getIntent().getStringExtra("total");

        bookingIdInput.setText(String.valueOf(bookingId));
        paymentAmountInput.setText(String.valueOf(paymentAmount));
        summaryCourt.setText(courtName);
        summaryDuration.setText(durationStr);
        summaryDate.setText(bookingDate);
        summaryTime.setText(bookingTime);
        summaryTotal.setText("Total: Rp" + totalAmount);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("QRIS", "Credit Card", "Debit Card"));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        payButton.setOnClickListener(v -> {
            String selectedMethod = (String) paymentMethodSpinner.getSelectedItem();

            ApiService apiService = ApiClient.getSessionClient().create(ApiService.class);
            PaymentRequest paymentRequest = new PaymentRequest(
                    bookingId,
                    "success",
                    paymentAmount,
                    selectedMethod
            );

            Call<ResponseBody> call = apiService.confirmPayment(paymentRequest);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(PaymentActivity.this, "Payment successful!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(PaymentActivity.this, "Failed to confirm payment.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(PaymentActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}