package com.example.iot_mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HealthData.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "HealthData";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HEART_RATE = "heart_rate";
    private static final String COLUMN_SPO2 = "spo2";
    private static final String COLUMN_SLEEP_MODE = "sleep_mode";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_BULB_STATUS = "bulb_status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HEART_RATE + " INTEGER, " +
                COLUMN_SPO2 + " INTEGER, " +
                COLUMN_SLEEP_MODE + " TEXT, " +
                COLUMN_BULB_STATUS + " TEXT, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(int heartRate, int spo2, String sleepMode, String bulbStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEART_RATE, heartRate);
        values.put(COLUMN_SPO2, spo2);
        values.put(COLUMN_SLEEP_MODE, sleepMode);
        values.put(COLUMN_BULB_STATUS, bulbStatus);

        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Insert failed.");
        } else {
            Log.d("DatabaseHelper", "Insert successful. Row ID: " + result);
        }
        //db.close();
    }

    public List<String[]> getAllData() {
        List<String[]> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SLEEP_MODE + ", " +
                COLUMN_HEART_RATE + ", " + COLUMN_SPO2 + ", " +
                COLUMN_TIMESTAMP + ", " + COLUMN_BULB_STATUS +
                " FROM " + TABLE_NAME, null);

        Log.d("DatabaseHelper", "Rows retrieved: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                String sleepMode = cursor.getString(0);
                String heartRate = cursor.getString(1);
                String spO2 = cursor.getString(2);
                String time = cursor.getString(3);
                String bulbStatus = cursor.getString(4);

                dataList.add(new String[]{sleepMode, heartRate, spO2, time, bulbStatus});
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();
        return dataList;
    }

    public String getLastSavedSleepMode() {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastSleepMode = null;
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_SLEEP_MODE},
                null,
                null,
                null,
                null,
                COLUMN_TIMESTAMP + " DESC",
                "1"
        );
        if (cursor != null && cursor.moveToFirst()) {
            lastSleepMode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_MODE));
        }
        if (cursor != null) {
            cursor.close();
        }
        return lastSleepMode;
    }
}
