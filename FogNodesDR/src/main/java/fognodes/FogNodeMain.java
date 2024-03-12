package fognodes;

import DBmanager.DataBaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class FogNodeMain {
    private static final Logger logger = LogManager.getLogger(FogNodeMain.class);

    public static void main(String[] args) {
        logger.info("CREATING NETWORK...");
        String broker = "tcp://localhost:1883"; // Mosquitto broker address (local)
        DataBaseManager dataBaseManager = new DataBaseManager();
        logger.info("DB initialized successfully");

        String[] fogNodes = readFogNodesFromFile("nodes.txt", "nodes");
        // For each node, create a MQTT client as a thread
        for (String nodeTopic : fogNodes) {
            String clientId = "FogNodeSubscriber_" + nodeTopic.substring(nodeTopic.lastIndexOf('/') + 1);
            String topic = "devices/" + nodeTopic + "/#";
            new Thread(new FogNodeSubscriber(broker, topic, clientId, dataBaseManager)).start();
        }
    }
    public static String[] readFogNodesFromFile(String filename, String routes) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].trim().equals(routes)) {
                    String[] nodes = parts[1].trim().split(", ");
                    return nodes;
                }
            }
        } catch (IOException e) {
            logger.error("Error reading fog nodes from file", e);
        }
        return new String[0];
    }
}
