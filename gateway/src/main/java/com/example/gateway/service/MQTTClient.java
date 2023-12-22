package com.example.gateway.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MQTTClient {
    private final String MQTT_ADDRESS = "tcp://127.0.0.1:1883";
    private final String TEMPERATURE_TOPIC = "temperature";
    private final String CONTROL_NODE_TOPIC = "control_node";
    private final String CONNECTION_TOPIC = "connection";
    private IMqttClient mqttClient;
    private final NodeHealthChecker nodeHealthChecker;
    private final TemperatureConsumer temperatureConsumer;



    public MQTTClient(NodeHealthChecker nodeHealthChecker, TemperatureConsumer temperatureConsumer) throws MqttException {
        this.nodeHealthChecker = nodeHealthChecker;
        this.temperatureConsumer = temperatureConsumer;

        String clientId = UUID.randomUUID().toString();
        this.mqttClient = new MqttClient(MQTT_ADDRESS, clientId, new MemoryPersistence());
        mqttClient.connect();
        mqttClient.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                System.out.println("connectionLost: " + cause.getMessage());
                cause.printStackTrace();
            }

            public void messageArrived(String topic, MqttMessage message) {
                if (topic.equals(CONNECTION_TOPIC)) {
                    String nodeId = new String(message.getPayload());
                    nodeHealthChecker.addNodeConnection(nodeId);
                }

                if (topic.equals(TEMPERATURE_TOPIC)) {
                    String messageContent = new String(message.getPayload());
                    temperatureConsumer.consumeTemperatureMessage(messageContent);
                }
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        String[] subscribeTopics = new String[] {CONNECTION_TOPIC, TEMPERATURE_TOPIC};
        int[] qos = {1, 1};
        mqttClient.subscribe(subscribeTopics, qos);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 5000)
    public void sendControlNodeMessage() throws MqttException {
        Long randomNumber = (long) (Math.random() * 100 + 1);
        String messageContent = randomNumber.toString();
        MqttMessage message = new MqttMessage(messageContent.getBytes());
        mqttClient.publish(CONTROL_NODE_TOPIC, message);
    }

}
