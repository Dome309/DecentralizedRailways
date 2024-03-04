package train.devices;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public abstract class Device implements MqttCallback {
    protected MqttClient client;
    protected String brokerUrl;
    protected String clientId;
    protected MemoryPersistence persistence;

    public Device(String brokerUrl, String clientId){
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.persistence = new MemoryPersistence();

        try {
            client = new MqttClient(brokerUrl, clientId, persistence);
            client.setCallback(this);
            client.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost: " + cause.getMessage());
        // You can handle reconnection logic here
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message arrived on topic: " + topic + " - Message: " + message.toString());
        // Handle incoming messages here
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Message delivery completed
    }

    public abstract void sendDataToFogNode(String node);
}
