package com.example.sportiva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookings;

    public BookingAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        String startTime = trimSeconds(booking.getBookingTime());
        int duration = booking.getBookingDuration();
        String endTime = calculateEndTime(startTime, duration);
        holder.courtTypeText.setText(booking.getCourtType());
        holder.dateText.setText(formatIsoDate(booking.getBookingDate()));
        holder.timeText.setText(startTime + " - " + endTime);
        holder.durationText.setText(duration + " minutes");
        holder.statusText.setText(booking.getStatus());
        holder.amountText.setText("Rp " + booking.getPaymentAmount());
        holder.paymentMethodText.setText(booking.getPaymentMethod());
        holder.dateCreatedText.setText(formatIsoDate(booking.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView courtTypeText;
        TextView dateText;
        TextView timeText;
        TextView durationText;
        TextView statusText;
        TextView amountText;
        TextView paymentMethodText;
        TextView dateCreatedText;

        BookingViewHolder(View itemView) {
            super(itemView);
            courtTypeText = itemView.findViewById(R.id.bookingTitle);
            dateText = itemView.findViewById(R.id.bookingDate);
            timeText = itemView.findViewById(R.id.bookingTime);
            durationText = itemView.findViewById(R.id.bookingDuration);
            statusText = itemView.findViewById(R.id.bookingStatus);
            amountText = itemView.findViewById(R.id.bookingAmount);
            paymentMethodText = itemView.findViewById(R.id.bookingMethod);
            dateCreatedText = itemView.findViewById(R.id.bookingCreated);
        }
    }
    private String formatIsoDate(String isoDate) {
        try {
            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date date = isoFormat.parse(isoDate);

            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoDate;
        }
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
            return ""; // fallback kalau error
        }
    }

    private String trimSeconds(String time) {
        if (time != null && time.length() >= 5 && time.contains(":")) {
            return time.substring(0, 5);
        }
        return time;
    }
}