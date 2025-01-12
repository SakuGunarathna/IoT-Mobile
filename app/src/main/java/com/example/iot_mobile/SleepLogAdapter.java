package com.example.iot_mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SleepLogAdapter extends RecyclerView.Adapter<SleepLogAdapter.ViewHolder> {
    private List<SleepLog> sleepLogs;

    public SleepLogAdapter(List<SleepLog> sleepLogs) {
        this.sleepLogs = sleepLogs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sleep_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SleepLog sleepLog = sleepLogs.get(position);

        holder.sleepModeTextView.setText(sleepLog.getSleepMode());
        holder.heartRateTextView.setText(sleepLog.getHeartRate());
        holder.spO2TextView.setText(sleepLog.getSpO2());
        holder.timeTextView.setText(sleepLog.getTime());
        holder.bulbStatusTextView.setText(sleepLog.getBulbStatus());
    }

    @Override
    public int getItemCount() {
        return sleepLogs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sleepModeTextView, heartRateTextView, spO2TextView, timeTextView, bulbStatusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            sleepModeTextView = itemView.findViewById(R.id.sleepMode);
            heartRateTextView = itemView.findViewById(R.id.heartRate);
            spO2TextView = itemView.findViewById(R.id.spO2);
            timeTextView = itemView.findViewById(R.id.time);
            bulbStatusTextView = itemView.findViewById(R.id.bulbStatus);
        }
    }
}
