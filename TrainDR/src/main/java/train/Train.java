package train;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Train {
    private String trainID;
    private String[] fogNodes;
    private int currentLocation;
    private double speed;
    private double temperature;
    private MqttClient client;

    public Train(String name, String[] fogNodes) {
        this.trainID = name;
        this.fogNodes = fogNodes;
        this.currentLocation = 0;
        this.speed = 0.0;
        this.temperature = 25.0;
        setupMQTT();
    }

    private void setupMQTT() {
        String broker = "tcp://localhost:1883";
        String clientId = trainID + "_client";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            client = new MqttClient(broker, clientId, persistence);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection lost: " + throwable.getMessage());
                }

                @Override
                public void messageArrived(String nodeTopic, MqttMessage mqttMessage) {
                    //Actually the client "train" doesn't receive any message
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("Delivery completed: " + iMqttDeliveryToken.isComplete());
                }
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Client "+clientId+" connecting to broker: "+broker);
            client.connect(connOpts);
            System.out.println("Client "+clientId+" successfully connected to broker "+broker);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void move() throws MqttException {
        while (currentLocation < fogNodes.length - 1) {
            String currentNode = fogNodes[currentLocation];
            String nextNode = fogNodes[currentLocation + 1];
            System.out.println("Train " + trainID + " has reached node " + currentNode);

            speedUpdate();
            tempUpdate();
            sendDataToFogNode(currentNode);

            System.out.println("Train " + trainID + " is heading to " + nextNode);
            System.out.println("------------------------------------------------");

            currentLocation++;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String lastNode = fogNodes[fogNodes.length - 1];
        System.out.println("Train " + trainID + " has arrived at its destination at node " + lastNode);

        speedUpdate();
        tempUpdate();
        sendDataToFogNode(lastNode);

        client.disconnect();
        System.out.println("Disconnected");
    }

    private void speedUpdate() {
        speed += (Math.random() * 10);
        System.out.println("Actual speed: " + String.format("%.2f", speed) + " km/h");
    }

    private void tempUpdate() {
        temperature += Math.random() * 2 - 1;
        System.out.println("Actual temperature: " + String.format("%.2f", temperature) + " °C");
    }

    private void sendDataToFogNode(String node) {
        String message = "Speed: " + String.format("%.2f", speed) + " km/h, Temperature: " + String.format("%.2f", temperature) + " °C";
        System.out.println("Sending data to..."+node);
        try {
            client.publish(node, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}