package org.example;

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
                System.out.println("deliveryComplete---------" + token.isComplete());
            }
        });

        client.subscribe(CONTROL_NODE_TOPIC, 1);

        //scheduled send health-check message
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(healthCheckTask(clientId), 0,10, TimeUnit.SECONDS);
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

}