package train.devices;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import train.UI.TrainCustomWaypoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static fognodes.UI.StartUI.frame;
import static train.TrainMain.map;

public class TrainManager extends Device {
    private static final String STOPS_ID_API = "https://www.dati.lombardia.it/resource/j5jz-kvqn.json";
    public static TrainCustomWaypoint trainWaypoint;
    private String trainManagerSubTopic = "trainManager";
    private String msg;
    private String trainID;

    public TrainManager(String clientId) {
        super(clientId);
        this.trainID = clientId;
    }

    public String printStationCoordinates(String station) {
        try {
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

            JSONArray jsonArray = new JSONArray(response.toString());

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
            e.printStackTrace();
        }

        return msg;
    }

    public static TrainCustomWaypoint createTrainWaypoint(double latitude, double longitude, String trainID) {
        return new TrainCustomWaypoint(latitude, longitude, trainID);
    }

    @Override
    public void sendDataToFogNode(String node) {
        String message = "Train: " + clientId;
        try {
            client.publish(mainTopic+node+"/"+trainManagerSubTopic, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
