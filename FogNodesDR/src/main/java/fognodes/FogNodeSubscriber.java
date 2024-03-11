package fognodes;

import DBmanager.DataBaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;

import java.util.Date;

class FogNodeSubscriber implements Runnable {
    private String brokerUrl;
    private String nodeTopic; //topic where each node is subscribed
    private String clientId;
    private static final Logger logger = LogManager.getLogger(FogNodeSubscriber.class);
    private DataBaseManager dataBaseManager;
    private String message;
    private String attribute;
    private String data;
    JSONObject jsonMessage;
    private int devicesExpected;

    public FogNodeSubscriber(String broker, String nodeTopic, String clientId, DataBaseManager dataBaseManager) {
        this.brokerUrl = broker;
        this.nodeTopic = nodeTopic;
        this.clientId = clientId;
        this.dataBaseManager = dataBaseManager;
        jsonMessage = new JSONObject();
        devicesExpected = 0;
    }

    //each node try the connection with the broker and subscribe to a topic which is different between each node
    public void run() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient client = new MqttClient(brokerUrl, clientId, persistence);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    logger.error(clientId+" connection lost: " + throwable.getMessage());
                }

                @Override
                public void messageArrived(String nodeTopic, MqttMessage mqttMessage) {
                    message = new String(mqttMessage.getPayload());
                    logger.info(clientId+" received message on nodeTopic: " + nodeTopic+ " Message: " + message);
                    splitMessage(message);
                    jsonMessage.put(attribute, data);
                    devicesExpected++;
                    if(devicesExpected==4){
                        dataBaseManager.setCollectionName(clientId, jsonMessage, new Date());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    //Actually each fog node doesn't send any message to other clients
                }
            });

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            logger.info(clientId+" connecting to broker: " + brokerUrl);
            client.connect(connOpts);
            logger.info(clientId+" connected to broker");

            client.subscribe(nodeTopic);
            logger.info(clientId+" subscribed to nodeTopic: " + nodeTopic);

        } catch (MqttException e) {
            logger.warn(clientId+" could not connect");
        }
    }
    private void splitMessage(String message){
        String[] splitMsg = message.split(": ");
        this.attribute = splitMsg[0];
        this.data = splitMsg[1];
    }
}
