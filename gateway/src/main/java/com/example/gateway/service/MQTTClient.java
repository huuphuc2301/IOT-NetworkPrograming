package com.example.gateway.service;

import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MQTTClient {
    private final String mqttAddress = "tcp://127.0.0.1:1883";
    private final String temperatureTopic = "temperature";
    private final String nodeStartTopic = "node_start";
    private final String controlNodeTopic = "control_node";
    private IMqttClient mqttClient;

    public MQTTClient() throws MqttException {
        String clientId = UUID.randomUUID().toString();
        this.mqttClient = new MqttClient(mqttAddress, clientId, new MemoryPersistence());
        mqttClient.connect();

    }

    @Scheduled(fixedRate = 5000)
    public void sendControlNodeMessage() throws MqttException {
        Long randomNumber = (long) (Math.random() * 100 + 1);
        String messageContent = randomNumber.toString();
        MqttMessage message = new MqttMessage(messageContent.getBytes());
        mqttClient.publish(controlNodeTopic, message);
    }
}
