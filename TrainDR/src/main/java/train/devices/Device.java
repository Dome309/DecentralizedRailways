package train.devices;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public abstract class Device {
    public static MqttClient client;

    public void setupMQTT() {
        String broker = "tcp://localhost:1883";
        String clientId = "SpeedControl_Client";
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
                    //Actually the client doesn't receive any message
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

    public abstract void sendDataToFogNode(String node);
}
