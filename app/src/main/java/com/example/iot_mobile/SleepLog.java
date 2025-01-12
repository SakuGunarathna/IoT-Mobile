package com.example.iot_mobile;
public class SleepLog {
    private String sleepMode;
    private String heartRate;
    private String spO2;
    private String time;
    private String bulbStatus;

    public SleepLog(String sleepMode, String heartRate, String spO2, String time, String bulbStatus) {
        this.sleepMode = sleepMode;
        this.heartRate = heartRate;
        this.spO2 = spO2;
        this.time = time;
        this.bulbStatus = bulbStatus;
    }

    public String getSleepMode() {
        return sleepMode;
    }
    public String getHeartRate() {
        return heartRate;
    }
    public String getSpO2() {
        return spO2;
    }
    public String getTime() {
        return time;
    }
    public String getBulbStatus() {
        return bulbStatus;
    }

}