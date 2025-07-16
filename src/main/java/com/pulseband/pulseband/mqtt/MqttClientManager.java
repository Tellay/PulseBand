package com.pulseband.pulseband.mqtt;

import com.pulseband.pulseband.decipher.Decipher;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;

public class MqttClientManager {
    private final MqttClient client;
    private MqttMessageHandler messageHandler;
    private final String bpmTopic;
    private final String alertTopic;
    private final MqttStatusListener listener;
    private boolean connected = false;

    public MqttClientManager(String brokerUrl, String clientId, String bpmTopic, String alertTopic, MqttStatusListener listener) throws MqttException {
        this.bpmTopic = bpmTopic;
        this.alertTopic = alertTopic;
        this.listener = listener;

        client = new MqttClient(brokerUrl, clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                connected = false;
                listener.onConnectionStatusChanged(false, "Mqtt connection lost: " + throwable.getMessage());
            }

            @Override
            public void messageArrived(String incomingTopic, MqttMessage message) {
                if (messageHandler != null) {
                    try {
                        String base64 = new String(message.getPayload(), StandardCharsets.UTF_8);
                        String decrypted = Decipher.decryptMessage(base64);
                        messageHandler.onMessageReceived(incomingTopic, decrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                        messageHandler.onMessageReceived(incomingTopic, "Error deciphering: " + e.getMessage());
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    public void connectAsync() {
        new Thread(() -> {
            try {
                MqttConnectOptions options = new MqttConnectOptions();
                options.setAutomaticReconnect(true);
                options.setCleanSession(true);

                client.connect(options);
                connected = true;
                listener.onConnectionStatusChanged(true, "Connected!");
                subscribe(bpmTopic);
                subscribe(alertTopic);
            } catch (MqttException e) {
                connected = false;
                listener.onConnectionStatusChanged(false, "Error!");
                e.printStackTrace();
            }
        }).start();
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void subscribe(String topic) throws MqttException {
        client.subscribe(topic);
        listener.onConnectionStatusChanged(true, "Connected and subscribed!");
    }

    public void disconnect() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
            connected = false;
            listener.onConnectionStatusChanged(false, "Disconnected!.");
        }
    }

    public void publish(String topic, String message) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
        client.publish(topic, mqttMessage);
    }

    public void setMessageHandler(MqttMessageHandler handler) {
        this.messageHandler = handler;
    }
}
