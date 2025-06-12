package com.example.sportiva;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private final List<String> allTimes;
    private final Set<String> availableTimes;

    public TimeSlotAdapter(List<String> allTimes, List<String> availableTimes) {
        this.allTimes = allTimes;
        this.availableTimes = new HashSet<>(availableTimes);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timeslot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String time = allTimes.get(position);
        boolean isAvailable = availableTimes.contains(time);

        holder.timeText.setText(time);

        if (!isAvailable) {
            holder.statusText.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.parseColor("#442222"));
        } else {
            holder.statusText.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return allTimes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView statusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.textTime);
            statusText = itemView.findViewById(R.id.textStatus);
        }
    }
}