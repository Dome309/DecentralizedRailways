package fognodes;

import DBmanager.DataBaseManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import java.util.concurrent.TimeUnit;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

class FogNodeSubscriber implements Runnable {
    private static final Logger logger = LogManager.getLogger(FogNodeSubscriber.class);
    private final String brokerUrl;
    private final String nodeTopic; //topic where each node is subscribed
    private final String clientId;
    private final String nodeName;
    private final DataBaseManager dataBaseManager;
    private String message;
    private String attribute;
    private String data;
    private JSONObject jsonMessage;
    private String trainId;
    private String responseTopic;
    private boolean dataIsWarn;
    private static final int TIMEOUT_SECONDS = 10;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> futureTask;

    public FogNodeSubscriber(String broker, String nodeTopic, String nodeName, DataBaseManager dataBaseManager) {
        this.brokerUrl = broker;
        this.nodeTopic = nodeTopic;
        this.nodeName = nodeName;
        this.clientId = "FogNode_" + nodeName;
        this.dataBaseManager = dataBaseManager;
        jsonMessage = new JSONObject();
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
                    splitMessage(message, client);
                    Level logLevel = dataIsWarn ? Level.WARN : Level.INFO;
                    jsonMessage.putAll(Map.of("logLevel", logLevel.name(), attribute, data));
                    //delete the old task if exist
                    if (futureTask != null) {
                        futureTask.cancel(false);
                    }
                    //create a new task for load data to db
                    futureTask = scheduler.schedule(this::loadDataToDatabase, TIMEOUT_SECONDS, TimeUnit.SECONDS);
                }
                private void loadDataToDatabase() {
                    dataBaseManager.setCollectionName(clientId, jsonMessage, new Date());
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

            client.subscribe(nodeTopic, 2);
            logger.info("{} subscribed to nodeTopic: {}", clientId, nodeTopic);

        } catch (MqttException e) {
            logger.warn("{} could not connect", clientId);
        }
    }

    //node splits the message receive for extract attribute and data
    private void splitMessage(String message, MqttClient client) throws ParseException {
        String[] splitMsg = message.split(": ");
        this.attribute = splitMsg[0];
        this.data = splitMsg[1];
        checkData(client, attribute, data);
    }

    //method for checking data values extracted
    private synchronized void checkData(MqttClient client, String attribute, String data) throws ParseException {
        String valueStr;
        double valueDouble;
        switch (attribute) {
            case "Train":
                this.responseTopic = data;
                this.trainId = extractValue(data);
                break;
            case "Speed":
                valueStr = extractValue(data);
                valueDouble = DecimalFormat.getNumberInstance().parse(valueStr).doubleValue();
                if (valueDouble < 15) {
                    sendResponse(client, trainId + " speed too low at " + nodeName, "speed");
                    this.dataIsWarn = true;
                }
                break;
            case "Temperature":
                valueStr = extractValue(data);
                valueDouble = DecimalFormat.getNumberInstance().parse(valueStr).doubleValue();
                if (valueDouble < 25) {
                    sendResponse(client, trainId + " temperature too low at " + nodeName, "temperature");
                    this.dataIsWarn = true;
                }
                break;
            case "Door status":
                valueStr = extractValue(data);
                if ("open".equals(valueStr)) {
                    sendResponse(client, trainId + " doors are open " + nodeName, "door");
                    this.dataIsWarn = true;
                }
                break;
            case "Light status":
                valueStr = extractValue(data);
                if ("off".equals(valueStr)) {
                    sendResponse(client, trainId + " lights are off " + nodeName, "light");
                    this.dataIsWarn = true;
                }
                break;
        }
    }

    //method for separating numbers from units
    private String extractValue(String data) {
        return data.split("\\s|_")[0];
    }

    //node can send a message to the relative train
    private void sendResponse(MqttClient client, String message, String type) {
        try {
            Date time = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String formattedTime = sdf.format(time);

            String responseMessage = "[" + formattedTime + "] " + message;
            client.publish("responseTopic/" + responseTopic + "/" + type, responseMessage.getBytes(), 2, false);
        } catch (MqttException e) {
            logger.error("{} failed to send response message", clientId);
        }
    }
}
