package com.example.iot_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DataListActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private SleepLogAdapter adapter;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);

        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);

        databaseHelper = new DatabaseHelper(this);

        if (databaseHelper == null) {
            Toast.makeText(this, "DatabaseHelper initialization failed", Toast.LENGTH_LONG).show();
            return;
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        loadDataIntoRecyclerView();
    }

    private void loadDataIntoRecyclerView() {
        if (databaseHelper != null) {
            List<String[]> dataList = databaseHelper.getAllData();
            if (dataList.isEmpty()) {
                Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter(null);
            } else {
                List<SleepLog> sleepLogs = new ArrayList<>();
                for (String[] data : dataList) {
                    String sleepMode = "Sleep Mode: " + data[0];
                    String heartRate = "Heart Rate: " + data[1] + " bpm";
                    String spO2 = "SpO2: " + data[2] + "%";
                    String time = "Time: " + data[3];
                    String bulbStatus = "Bulb: " + data[4];

                    sleepLogs.add(new SleepLog(sleepMode, heartRate, spO2, time, bulbStatus));
                }
                System.out.println("Number of sleep logs: " + sleepLogs.size());

                if (!sleepLogs.isEmpty()) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    adapter = new SleepLogAdapter(sleepLogs);
                    recyclerView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                }
            }
        } else {
            Toast.makeText(this, "DatabaseHelper is null", Toast.LENGTH_LONG).show();
        }
    }
}
