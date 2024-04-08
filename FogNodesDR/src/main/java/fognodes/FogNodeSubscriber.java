package fognodes;

import DBmanager.DataBaseManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

class FogNodeSubscriber implements Runnable {
    private String brokerUrl;
    private String nodeTopic; //topic where each node is subscribed
    private String clientId;
    private static final Logger logger = LogManager.getLogger(FogNodeSubscriber.class);
    private DataBaseManager dataBaseManager;
    private String message;
    private String attribute;
    private String data;
    private JSONObject jsonMessage;
    private int devicesExpected;
    private String trainId;

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
                public void messageArrived(String nodeTopic, MqttMessage mqttMessage) throws ParseException {
                    message = new String(mqttMessage.getPayload());
                    logger.info(clientId+" received message on nodeTopic: " + nodeTopic+ " Message: " + message);
                    Level logLevel = logger.getLevel();
                    splitMessage(message, client);
                    jsonMessage.putAll(Map.of(
                            "logLevel", logLevel.name(),
                            attribute, data));
                    devicesExpected++;
                    if(devicesExpected==5){
                        dataBaseManager.setCollectionName(clientId, jsonMessage, new Date());
                        devicesExpected = 0;
                    }


                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
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

    private void splitMessage(String message, MqttClient client) throws ParseException {
        String[] splitMsg = message.split(": ");
        this.attribute = splitMsg[0];
        this.data = splitMsg[1];
        checkData(attribute, data, client);
    }

    //TODO improve this method for error management from data received
    private synchronized void checkData(String attribute, String data, MqttClient client) throws ParseException {
        String valueStr;

        switch (attribute){
            case "Train":
                this.trainId = extractValue(data);
                break;
            case "Speed":
                valueStr = extractValue(data);
                double valueDouble = DecimalFormat.getNumberInstance().parse(valueStr).doubleValue();
                if (valueDouble < 15){
                    String responseMessage = trainId+" speed too low at "+clientId;
                    try {
                        client.publish("responseTopic", new MqttMessage(responseMessage.getBytes()));
                    } catch (MqttException e) {
                        logger.error(clientId+" failed to send response message");
                    }
                }
                break;
            case "Temperature":
                valueStr = extractValue(data);
                break;
            case "Door status":
                valueStr = extractValue(data);

                break;
            case "Light status":
                valueStr = extractValue(data);

                break;
        }
    }

    private String extractValue(String data){
        String[] splitValue = data.split(" ");
        String value = splitValue[0];
        return value;
    }
}
