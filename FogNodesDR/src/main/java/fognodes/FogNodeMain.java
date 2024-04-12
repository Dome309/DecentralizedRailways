package fognodes;

import DBmanager.DataBaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static fognodes.UI.StartUI.apiUrl;

public class FogNodeMain {
    private static final Logger logger = LogManager.getLogger(FogNodeMain.class);

    public static void main(String[] args) {
        StartNetwork(apiUrl);
    }

    public static void StartNetwork(String apiUrl) {
        try {
            logger.info("CREATING NETWORK...");
            String broker = "tcp://localhost:1883"; // Mosquitto broker address (local)
            DataBaseManager dataBaseManager = new DataBaseManager();
            logger.info("DB initialized successfully");
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(response.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String nodeName = jsonObject.getString("stop_name");
                String clientId = "FogNodeSubscriber_" + nodeName.substring(nodeName.lastIndexOf('/') + 1);
                String topic = "devices/" + nodeName + "/#";
                new Thread(new FogNodeSubscriber(broker, topic, clientId, dataBaseManager)).start();
            }

            connection.disconnect();
        } catch (IOException e) {
            logger.error("Network creation failed");
            e.printStackTrace();
        }
    }
}
