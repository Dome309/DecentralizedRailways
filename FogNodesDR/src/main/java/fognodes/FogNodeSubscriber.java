package fognodes;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

class FogNodeSubscriber implements Runnable {
    private String broker;
    private String nodeTopic;
    private String clientId;

    public FogNodeSubscriber(String broker, String nodeTopic, String clientId) {
        this.broker = broker;
        this.nodeTopic = nodeTopic;
        this.clientId = clientId;
    }

    public void run() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println(clientId+" connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println(clientId+" connected to broker");

            client.subscribe(nodeTopic, (topic, message) -> {
                System.out.println(clientId+" received message on nodeTopic: " + topic);
                System.out.println("Message: " + new String(message.getPayload()));
            });

            System.out.println(clientId+" subscribed to nodeTopic: " + nodeTopic);
        } catch (MqttException me) {

        }
    }
}
