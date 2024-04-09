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
                    logger.error("{} connection lost: {}", clientId, throwable.getMessage());
                }

                @Override
                public void messageArrived(String nodeTopic, MqttMessage mqttMessage) throws ParseException {
                    message = new String(mqttMessage.getPayload());
                    logger.info("{} received message on nodeTopic: {} Message: {}", clientId, nodeTopic, message);
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
            logger.info("{} connecting to broker: {}", clientId, brokerUrl);
            client.connect(connOpts);
            logger.info("{} connected to broker", clientId);

            client.subscribe(nodeTopic);
            logger.info("{} subscribed to nodeTopic: {}", clientId, nodeTopic);

        } catch (MqttException e) {
            logger.warn("{} could not connect", clientId);
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
        double valueDouble;

        switch (attribute){
            case "Train":
                this.trainId = extractValue(data);
                break;
            case "Speed":
                valueStr = extractValue(data);
                valueDouble = DecimalFormat.getNumberInstance().parse(valueStr).doubleValue();
                if (valueDouble < 15){
                    String responseMessage = trainId+" speed too low at "+clientId;
                    try {
                        client.publish("responseTopic", new MqttMessage(responseMessage.getBytes()));
                    } catch (MqttException e) {
                        logger.error("{} failed to send speed response message", clientId);
                    }
                }
                break;
            case "Temperature":
                valueStr = extractValue(data);
                valueDouble = DecimalFormat.getNumberInstance().parse(valueStr).doubleValue();
                if (valueDouble < 25){
                    String responseMessage = trainId+" temperature too low at "+clientId;
                    try {
                        client.publish("responseTopic", new MqttMessage(responseMessage.getBytes()));
                    } catch (MqttException e) {
                        logger.error("{} failed to send temperature response message", clientId);
                    }
                }
                break;
            case "Door status":
                valueStr = extractValue(data);
                if (valueStr.equals("open")){
                    String responseMessage = trainId+" doors are open "+clientId;
                    try {
                        client.publish("responseTopic", new MqttMessage(responseMessage.getBytes()));
                    } catch (MqttException e) {
                        logger.error("{} failed to send door status response message", clientId);
                    }
                }
                break;
            case "Light status":
                valueStr = extractValue(data);
                if (valueStr.equals("off")){
                    String responseMessage = trainId+" lights are off "+clientId;
                    try {
                        client.publish("responseTopic", new MqttMessage(responseMessage.getBytes()));
                    } catch (MqttException e) {
                        logger.error("{} failed to send light status response message", clientId);
                    }
                }
                break;
        }
    }

    private String extractValue(String data){
        String[] splitValue = data.split(" ");
        return splitValue[0];
    }
}
