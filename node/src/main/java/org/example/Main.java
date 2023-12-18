package org.example;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main {
    private static final String MQTT_ADDRESS = "tcp://127.0.0.1:1883";
    private static final String TEMPERATURE_TOPIC = "temperature";
    private static final String NODE_START_TOPIC = "node_start";
    private static final String CONTROL_NODE_TOPIC = "control_node";

    public static void main(String[] args) throws MqttException {
        String clientId = args[0];
        MqttClient client = new MqttClient(MQTT_ADDRESS, clientId, new MemoryPersistence());
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
    }

    private static void handleControlNodeTopic(MqttMessage message) {
        System.out.println(new String(message.getPayload()));
    }


}