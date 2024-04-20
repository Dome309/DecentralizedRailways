package train.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import train.UI.TrainCustomWaypoint;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static fognodes.UI.StartUI.*;
import static train.TrainMain.map;
import static train.UI.TrainWaypointRender.getColorForLabel;

public class TrainManager extends Device {
    private static final String STOPS_ID_API = "https://www.dati.lombardia.it/resource/j5jz-kvqn.json";
    public static TrainCustomWaypoint trainWaypoint;
    private String trainManagerSubTopic = "trainManager";
    private String msg;
    private String trainID;
    private static final Logger logger = LogManager.getLogger(TrainManager.class);

    public TrainManager(String clientId) throws MqttException {
        super(clientId + "_manager");
        this.trainID = clientId;
        client.subscribe("responseTopic/" + clientId + "_manager" + "/#");
    }

    public String printStationCoordinates(String station) {
        try {
            JSONArray jsonArray = getStationCoordinates(station);

            if (!jsonArray.isEmpty()) {
                JSONObject stationInfo = jsonArray.getJSONObject(0);
                double latitude = stationInfo.getDouble("stop_lat");
                double longitude = stationInfo.getDouble("stop_lon");
                frame.repaint();
                trainWaypoint = createTrainWaypoint(latitude, longitude, trainID);
                map.addWaypoints(trainWaypoint);
                msg = "Latitude - " + latitude + ", Longitude - " + longitude;

            } else {
                System.out.println("Station " + station + " not found.");
            }
        } catch (Exception e) {
            logger.error("API error");
        }

        return msg;
    }

    private JSONArray getStationCoordinates(String station) throws IOException {
        String apiUrl = STOPS_ID_API + "?stop_name=" + station.replace(" ", "%20");
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return new JSONArray(response.toString());
    }

    public static TrainCustomWaypoint createTrainWaypoint(double latitude, double longitude, String trainID) {
        return new TrainCustomWaypoint(latitude, longitude, trainID);
    }

    @Override
    public void sendDataToFogNode(String node) {
        String message = "Train: " + clientId;
        try {
            client.publish(mainTopic + node + "/" + trainManagerSubTopic, message.getBytes(), 1, false);
        } catch (MqttException e) {
            logger.error("Train manager publish failed");
        }
    }

    @Override
    public void messageArrived(String node, MqttMessage mqttMessage) {
        String message = new String(mqttMessage.getPayload());
        logger.info("{} received message on nodeTopic: {} Message: {}", clientId, node, message);
        try {
            Color color = getColorForLabel(trainID);
            SimpleAttributeSet squareAttribute = new SimpleAttributeSet();
            StyleConstants.setForeground(squareAttribute, color);
            errorTextAreaDocument.insertString(errorTextAreaDocument.getLength(), "■", squareAttribute);
            errorTextAreaDocument.insertString(errorTextAreaDocument.getLength(), " " + message + "\n", null);
        } catch (BadLocationException e) {
            logger.error(e.getMessage());
        }
    }
}
