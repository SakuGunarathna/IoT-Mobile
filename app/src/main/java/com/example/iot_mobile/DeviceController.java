package com.example.iot_mobile;

import android.content.Context;
import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.Arrays;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class is responsible for
 * 1. Communicating with MQTT broker
 * 2. Communicating with SmartThings APIs
 */
public class DeviceController {

    private final MqttAndroidClient client;
    private static final String SERVER_URI = "tcp://test.mosquitto.org:1883";
    private static final String TAG = "DeviceController";
    private final ApiService apiService;
    private static final String BASE_URL = "https://api.smartthings.com/v1/devices/adf6d117-929c-4759-843c-1a143fb2f950/commands";
    private static final String AUTH_TOKEN = "Bearer f1ed736f-afaf-489d-a982-0d7769946cc4";

    public interface MqttCallback {
        void onMessageReceived(int heartRate, int spo2, String sleepMode);
    }

    public DeviceController(Context context) {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context.getApplicationContext(), SERVER_URI, clientId);
        apiService = ApiClient.createService();
    }

    public void connectToMqtt() {
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connected to: " + SERVER_URI);
                    subscribeToTopic("sensor/data");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failed to connect to: " + SERVER_URI, exception);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToTopic(String topic) {
        try {
            client.subscribe(topic, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setMqttCallback(MqttCallback callback) {
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "Connection lost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String[] parts = new String(message.getPayload()).split(", ");
                int heartRate = Integer.parseInt(parts[0].split(": ")[1].replace(" BPM", ""));
                int spo2 = Integer.parseInt(parts[1].split(": ")[1].replace("%", ""));
                String sleepMode = parts[2].split(": ")[1];

                System.out.println("HearRate from Mqtt: " + heartRate);
                System.out.println("SpO2 from Mqtt: " + spo2);
                System.out.println("SleepMode from Mqtt: " + sleepMode);

                callback.onMessageReceived(heartRate, spo2, sleepMode);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
            }
        });
    }

    public void sendCommand(String command) {
        CommandRequest commandRequest = new CommandRequest(
                Collections.singletonList(new Command("main", "switch", command))
        );

        apiService.sendCommand(BASE_URL, AUTH_TOKEN, commandRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to send command");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error sending command", t);
            }
        });
    }

    public void sendDimCommand(int level) {
        CommandRequest commandRequest = new CommandRequest(
                Collections.singletonList(new Command("main", "switchLevel", "setLevel", Arrays.asList(level)))
        );

        apiService.sendCommand(BASE_URL, AUTH_TOKEN, commandRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to send dim command");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error sending dim command", t);
            }
        });
    }
}
