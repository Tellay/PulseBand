package com.pulseband.pulseband.mqtt;

public interface MqttMessageHandler {
    void onMessageReceived(String topic, String message);
}
