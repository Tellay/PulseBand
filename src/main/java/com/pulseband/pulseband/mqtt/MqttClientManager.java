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

    private final MqttConfig mqttConfig = new MqttConfig();

    public MqttClientManager(String brokerUrl, String clientId, String bpmTopic, String alertTopic, String decryptedBpmTopic, String decryptedAlertTopic, MqttStatusListener listener) throws MqttException {
        this.bpmTopic = bpmTopic;
        this.alertTopic = alertTopic;
        this.listener = listener;

        client = new MqttClient(brokerUrl, clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                connected = false;
                listener.onConnectionStatusChanged(false, "Connection lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload());
                try {
                    String finalMessage = mqttConfig.shouldDecrypt(topic)
                            ? Decipher.decryptMessage(payload)
                            : payload;

                    messageHandler.onMessageReceived(topic, finalMessage);
                } catch (Exception e) {
                    System.err.println("Erro a processar mensagem no tópico " + topic + ": " + e.getMessage());
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
                //subscribe(decryptedTopic);
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