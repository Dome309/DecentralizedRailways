package fognodes;

import DBmanager.DataBaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FogNodeMain {
    //Train route definition
    public static String[] fogNodes = {"node_A", "node_B", "node_C", "node_D", "node_E"};
    private static final Logger logger = LogManager.getLogger(FogNodeMain.class);
    public static void main(String[] args) {
        String broker = "tcp://localhost:1883"; //Mosquitto broker address (local)
        DataBaseManager dataBaseManager = new DataBaseManager();
        logger.info("DB initialized successfully");
        //for each node is created a MQTT client as a thread
        for (String nodeTopic : fogNodes) {
            String clientId = "FogNodeSubscriber_" + nodeTopic.substring(nodeTopic.lastIndexOf('/') + 1);
            String topic = "devices/"+nodeTopic+ "/#";
            new Thread(new FogNodeSubscriber(broker, topic, clientId, dataBaseManager)).start();
        }
    }
}
