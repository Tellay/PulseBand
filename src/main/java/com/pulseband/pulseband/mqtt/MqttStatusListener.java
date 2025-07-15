package com.pulseband.pulseband.mqtt;

public interface MqttStatusListener {
    void onConnectionStatusChanged(boolean isConnected, String statusMessage);
}
