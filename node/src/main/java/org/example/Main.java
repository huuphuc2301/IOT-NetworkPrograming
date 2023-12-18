package org.example;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main {
    private static final String MQTT_ADDRESS = "tcp://127.0.0.1:1883";
    private static final String TEMPERATURE_TOPIC = "temperature";
    private static final String NODE_START_TOPIC = "node_start";
    public static void main(String[] args) throws MqttException {
        String clientId = args[0];
        MqttClient mqttClient = new MqttClient(MQTT_ADDRESS, clientId, new MemoryPersistence());
        mqttClient.connect();
        mqttClient.subscribe(TEMPERATURE_TOPIC, 1);
        System.out.println("abc");
    }
}