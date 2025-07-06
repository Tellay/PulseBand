package com.pulseband.pulseband.mqtt;

public interface MqttStatusListener {
    void onConnectionStatusChanged(String status);
}
