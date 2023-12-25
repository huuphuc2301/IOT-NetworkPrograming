package org.example;

import java.time.Instant;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Main {
    private static final String MQTT_ADDRESS = "tcp://127.0.0.1:1883";
    private static final String TEMPERATURE_TOPIC = "temperature";
    private static final String CONTROL_NODE_TOPIC = "control_node";
    private static final String CONNECTION_TOPIC = "connection";
    private static IMqttClient client;
    private static Integer currentTemperature;

    public static void main(String[] args) throws MqttException {
        String clientId = args[0];
        System.out.println("Node " + clientId + " started");
        client = new MqttClient(MQTT_ADDRESS, clientId, new MemoryPersistence());
        client.connect();

        client.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                System.out.println("connectionLost: " + cause.getMessage());
            }

            public void messageArrived(String topic, MqttMessage message) {
                if (topic.equals(CONTROL_NODE_TOPIC)) {
                    handleControlNodeTopic(message);
                }
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        client.subscribe(CONTROL_NODE_TOPIC, 1);

        //scheduled send health-check message
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(healthCheckTask(clientId), 0, 10, TimeUnit.SECONDS);

        //schedule send temperature
        executorService.scheduleAtFixedRate(sendTemperatureTask(clientId), 5,
            10, TimeUnit.SECONDS);
    }

    private static void handleControlNodeTopic(MqttMessage message) {
        System.out.println("Gateway sent: " + new String(message.getPayload()));
    }

    private static Runnable healthCheckTask(String clientId) {
        return () -> {
            MqttMessage message = new MqttMessage(clientId.getBytes());
            try {
                System.out.println("Sent health check to gateway");
                client.publish(CONNECTION_TOPIC, message);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Runnable sendTemperatureTask(String clientId) {
        return () -> {
            var now = Instant.now();
            currentTemperature = randomTemperature(currentTemperature);
            String messageContent = clientId + " " + currentTemperature + " " + now;
            MqttMessage message = new MqttMessage(messageContent.getBytes());
            try {
                System.out.println("Sent temperature to gateway: " + currentTemperature);
                client.publish(TEMPERATURE_TOPIC, message);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Integer randomTemperature(Integer preTemperature) {
        if (preTemperature == null) {
            return randomNumber(20, 25);
        }

        return randomNumber(preTemperature - 1, preTemperature + 1);
    }

    private static Integer randomNumber(Integer min, Integer max) {
        return (int) ((Math.random() * (max - min + 1)) + min);
    }

}