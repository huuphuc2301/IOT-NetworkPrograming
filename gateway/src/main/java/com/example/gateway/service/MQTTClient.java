package com.example.gateway.service;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MQTTClient {
    private final String MQTT_ADDRESS = "tcp://127.0.0.1:1883";
    private final String TEMPERATURE_TOPIC = "temperature";
    private final String NODE_START_TOPIC = "node_start";
    private final String CONTROL_NODE_TOPIC = "control_node";
    private IMqttClient mqttClient;

    public MQTTClient() throws MqttException {
        String clientId = UUID.randomUUID().toString();
        this.mqttClient = new MqttClient(MQTT_ADDRESS, clientId, new MemoryPersistence());
        mqttClient.connect();
        mqttClient.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                System.out.println("connectionLost: " + cause.getMessage());
            }

            public void messageArrived(String topic, MqttMessage message) {
                if (topic.equals(NODE_START_TOPIC)) {
                    System.out.println("Node " + new String(message.getPayload()) + " started");
                }
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
        mqttClient.subscribe(NODE_START_TOPIC, 1);
    }

    @Scheduled(fixedRate = 5000)
    public void sendControlNodeMessage() throws MqttException {
        Long randomNumber = (long) (Math.random() * 100 + 1);
        String messageContent = randomNumber.toString();
        MqttMessage message = new MqttMessage(messageContent.getBytes());
        mqttClient.publish(CONTROL_NODE_TOPIC, message);
    }
}
