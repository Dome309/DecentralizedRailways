package fognodes;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

class FogNodeSubscriber implements Runnable {
    private String brokerUrl;
    private String nodeTopic; //topic where each node is subscribed
    private String clientId;

    public FogNodeSubscriber(String broker, String nodeTopic, String clientId) {
        this.brokerUrl = broker;
        this.nodeTopic = nodeTopic;
        this.clientId = clientId;
    }

    //each node try the connection with the broker and subscribe to a topic which is different between each node
    public void run() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient client = new MqttClient(brokerUrl, clientId, persistence);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println(clientId+" connection lost: " + throwable.getMessage());
                }

                @Override
                public void messageArrived(String nodeTopic, MqttMessage mqttMessage) {
                    System.out.println(clientId+" received message on nodeTopic: " + nodeTopic);
                    System.out.println("Message: " + new String(mqttMessage.getPayload()));
                    //TODO: improve message display
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    //Actually each fog node doesn't send any message to other clients
                }
            });

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println(clientId+" connecting to broker: " + brokerUrl);
            client.connect(connOpts);
            System.out.println(clientId+" connected to broker");

            client.subscribe(nodeTopic);
            System.out.println(clientId+" subscribed to nodeTopic: " + nodeTopic);
        } catch (MqttException e) {
        }
    }
}
