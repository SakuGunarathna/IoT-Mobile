package com.example.iot_mobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private TextView heartrate;
    private TextView spo2;

    private MqttAndroidClient client;
    private static final String SERVER_URI = "tcp://test.mosquitto.org:1883";
    private static final String TAG ="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        heartrate = (TextView) findViewById(R.id.heartRate);
        spo2 = (TextView) findViewById(R.id.spo2);

        connect();

        client.setCallback(new MqttCallbackExtended() {

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String newMessage = new String(message.getPayload());
                //System.out.println("Incoming message: " + newMessage);

                String[] parts = newMessage.split(", ");
                String heartRateStr = parts[0].split(": ")[1].replace(" BPM", "");
                String spo2Str = parts[1].split(": ")[1].replace("%", "");

                int heartRate = Integer.parseInt(heartRateStr);
                int spo2val = Integer.parseInt(spo2Str);

                System.out.println("Heart Rate: " + heartRate);
                System.out.println("SpO2: " + spo2);
                heartrate.setText("Heart Rate : " + heartRate+ " bpm");
                spo2.setText("SpO2 : " + spo2val+ " %");


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    System.out.println("Reconnected to : " + serverURI);
                } else {
                    System.out.println("Connected to: " + serverURI);
                    subscribe("sensor/data");

                }

            }

        });
    }

    private void connect(){ String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), SERVER_URI, clientId);
        try { IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() { @Override public void onSuccess(IMqttToken asyncActionToken) {
                // We are connected Log.d(TAG, "onSuccess");
                System.out.println(TAG + " Success. Connected to " + SERVER_URI);
            }
                @Override public void onFailure(IMqttToken asyncActionToken, Throwable exception) { // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    System.out.println(exception);
                    System.out.println(TAG + " Oh no! Failed to connect to " + SERVER_URI);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println(e);

        }
    }

    private void subscribe(String topicToSubscribe) { final String topic = topicToSubscribe; int qos = 1;
        try { IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() { @Override public void onSuccess(IMqttToken asyncActionToken) { System.out.println("Subscription successful to topic: " + topic);
            }
                @Override public void onFailure(IMqttToken asyncActionToken, Throwable exception) { System.out.println("Failed to subscribe to topic: " + topic);
                    // The subscription could not be performed, maybe the user was not // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) { e.printStackTrace();
        }
    }
}