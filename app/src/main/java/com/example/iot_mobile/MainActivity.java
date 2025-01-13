package com.example.iot_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private TextView heartrate, spo2, mode, bulbstatus;
    private ImageButton btnOn, btnOff, btnDim;
    private Switch switchMode;
    private ImageView eyesIcon;
    private FloatingActionButton fab;
    private boolean isManualMode = false;
    private DeviceController deviceController;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(MainActivity.this);
        setContentView(R.layout.activity_main);
        initializeUI();
        setupManualMode();
        deviceController = new DeviceController(this);
        switchMode.setChecked(isManualMode);
        switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isManualMode = false;
            if (isChecked) {
                setupAutoMode();
            } else {
                setupManualMode();
            }
        });
    }
    private void initializeUI() {
        btnOn = findViewById(R.id.btnOn);
        btnOff = findViewById(R.id.btnOff);
        btnDim = findViewById(R.id.btnDim);
        bulbstatus = findViewById(R.id.bulbstatus);
        eyesIcon = findViewById(R.id.eyesIcon);
        heartrate = findViewById(R.id.heartRate);
        spo2 = findViewById(R.id.spo2);
        mode = findViewById(R.id.mode);
        switchMode = findViewById(R.id.switchMode);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DataListActivity.class));

        });
    }
    private void setupAutoMode() {
        //Toast.makeText(MainActivity.this, "Auto", Toast.LENGTH_SHORT).show();
        switchMode.setText("Auto");
        heartrate.setVisibility(View.VISIBLE);
        spo2.setVisibility(View.VISIBLE);
        eyesIcon.setVisibility(View.INVISIBLE);
        mode.setVisibility(View.VISIBLE);
        bulbstatus.setVisibility(View.INVISIBLE);
        btnOn.setEnabled(false);
        btnOff.setEnabled(false);
        btnDim.setEnabled(false);
        btnOn.setImageResource(R.drawable.ic_onmodeauto);
        btnOff.setImageResource(R.drawable.ic_offmodeauto);
        btnDim.setImageResource(R.drawable.ic_dimmodeauto);
        deviceController.connectToMqtt();
        deviceController.setMqttCallback((heartRate, spo2val, sleepMode) -> {
            heartrate.setText("Heart Rate: " + heartRate + " bpm");
            spo2.setText("SpO2: " + spo2val + " %");
            mode.setText("Sleep Mode: " + sleepMode);
            saveDataIfSleepModeChanged(heartRate, spo2val, sleepMode);
            handleSleepMode(sleepMode);
        });
    }
    private void setupManualMode() {
        //Toast.makeText(MainActivity.this, "Manual", Toast.LENGTH_SHORT).show();
        switchMode.setText("Manual");
        heartrate.setVisibility(View.INVISIBLE);
        spo2.setVisibility(View.INVISIBLE);
        eyesIcon.setVisibility(View.INVISIBLE);
        mode.setVisibility(View.INVISIBLE);
        bulbstatus.setVisibility(View.INVISIBLE);
        btnOn.setEnabled(true);
        btnOff.setEnabled(true);
        btnDim.setEnabled(true);
        btnOn.setImageResource(R.drawable.ic_onmodemanual);
        btnOff.setImageResource(R.drawable.ic_offmodemanual);
        btnDim.setImageResource(R.drawable.ic_dimmodemanual);
        spo2.setText("SpO2 : 00 %");
        heartrate.setText(" Heart Rate : 00 bpm");
        mode.setText("Sleep Mode : None");
        btnOn.setOnClickListener(v -> {
            deviceController.sendDimCommand(100);
            bulbstatus.setVisibility(View.VISIBLE);
            bulbstatus.setText("Your Smart Bulb is On");
        });

        btnDim.setOnClickListener(v -> {
            deviceController.sendDimCommand(2);
            bulbstatus.setVisibility(View.VISIBLE);
            bulbstatus.setText("Your Smart Bulb is Dim");
        });

        btnOff.setOnClickListener(v -> {
            deviceController.sendCommand("off");
            bulbstatus.setVisibility(View.VISIBLE);
            bulbstatus.setText("Your Smart Bulb is Off");
        });
    }
    private void handleSleepMode(String sleepMode) {
        switch (sleepMode) {
            case "Awake":
                deviceController.sendDimCommand(100);
                bulbstatus.setVisibility(View.VISIBLE);
                bulbstatus.setText("Your Smart Bulb is On");
                eyesIcon.setVisibility(View.VISIBLE);
                break;
            case "LightSleep":
                deviceController.sendDimCommand(2);
                bulbstatus.setVisibility(View.VISIBLE);
                bulbstatus.setText("Your Smart Bulb is Dim");
                break;
            default:
                deviceController.sendCommand("off");
                bulbstatus.setVisibility(View.VISIBLE);
                bulbstatus.setText("Your Smart Bulb is Off");
                break;
        }
    }


    private void saveDataIfSleepModeChanged(int heartRate, int spo2val, String sleepMode) {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String lastSavedSleepMode = databaseHelper.getLastSavedSleepMode();
        String bulbStatus = getBulbStatus(sleepMode);

        if (!sleepMode.equals(lastSavedSleepMode)) {
            databaseHelper.insertData(heartRate, spo2val, sleepMode, bulbStatus);
            Log.d("MainActivity", "Data inserted into database.");
            Toast.makeText(this, "Data saved to database", Toast.LENGTH_SHORT).show();

        } else {

            Log.d("MainActivity", "Sleep mode unchanged, data not saved.");
            Toast.makeText(this, "Sleep mode unchanged, data not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBulbStatus(String sleepMode) {
        switch (sleepMode) {
            case "Awake":
                return "ON";
            case "LightSleep":
                return "DIM";
            default:
                return "OFF";
        }

    }
}
