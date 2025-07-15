package com.pulseband.pulseband.mqtt;

import com.pulseband.pulseband.decipher.Decypher;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;

public class MqttClientManager {
    private final MqttClient client;
    private MqttMessageHandler messageHandler;
    private final String topic;
    private final String appTopic;
    private final MqttStatusListener listener;

    public MqttClientManager(String brokerUrl, String clientId, String topic, String appTopic, MqttStatusListener listener) throws MqttException {
        this.topic = topic;
        this.appTopic = appTopic;
        this.listener = listener;

        client = new MqttClient(brokerUrl, clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                listener.onConnectionStatusChanged("Connection lost: " + throwable.getMessage());
            }

            @Override
            public void messageArrived(String incomingTopic, MqttMessage message) {
                if (messageHandler != null) {
                    try {
                        String base64 = new String(message.getPayload(), StandardCharsets.UTF_8);
                        String decrypted = Decypher.decryptMessage(base64);
                        messageHandler.onMessageReceived(incomingTopic, decrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                        messageHandler.onMessageReceived(incomingTopic, "Erro ao decifrar: " + e.getMessage());
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    public void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        client.connect(options);
        listener.onConnectionStatusChanged("Connected to the MQTT broker!");
        subscribe(topic);
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void subscribe(String topic) throws MqttException {
        client.subscribe(topic);
        listener.onConnectionStatusChanged("Subscribed to the topic: " + topic);
    }

    public void disconnect() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
            listener.onConnectionStatusChanged("Desconnected from the MQTT broker.");
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
