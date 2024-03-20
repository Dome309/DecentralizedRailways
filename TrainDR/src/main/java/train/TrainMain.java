package train;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class TrainMain {
    private static final Logger logger = LogManager.getLogger(TrainMain.class);
    private static final String ROUTES_ID_API = "https://www.dati.lombardia.it/resource/asyc-aywm.json?$limit=50000";
    private static final String STOPS_ID_API = "https://www.dati.lombardia.it/resource/j5jz-kvqn.json?stop_id=";

    public static void main(String[] args) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(ROUTES_ID_API).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            Map<String, String> routeToFirstTripMap = new HashMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String routeId = jsonObject.getString("route_id");
                String tripId = jsonObject.getString("trip_id");

                if (!routeToFirstTripMap.containsKey(routeId)) {
                    routeToFirstTripMap.put(routeId, tripId);
                }
            }

            List<Thread> threads = new ArrayList<>();
            for (Map.Entry<String, String> entry : routeToFirstTripMap.entrySet()) {
                String routeId = entry.getKey();
                String firstTripId = entry.getValue();

                String secondApiUrl = "https://www.dati.lombardia.it/resource/4z9q-hrcb.json?trip_id=" + firstTripId;
                HttpURLConnection secondConn = (HttpURLConnection) new URL(secondApiUrl).openConnection();
                secondConn.setRequestMethod("GET");

                BufferedReader secondReader = new BufferedReader(new InputStreamReader(secondConn.getInputStream()));
                StringBuilder secondResponse = new StringBuilder();
                String secondLine;
                while ((secondLine = secondReader.readLine()) != null) {
                    secondResponse.append(secondLine);
                }
                secondReader.close();

                JSONArray secondJsonArray = new JSONArray(secondResponse.toString());
                System.out.println("Train: " + routeId + " with trip Id: " + firstTripId);

                //TreeMap to maintain the order of arrival times
                Map<String, String> arrivalTimesAndStopIds = new TreeMap<>();
                for (int i = 0; i < secondJsonArray.length(); i++) {
                    JSONObject secondJsonObject = secondJsonArray.getJSONObject(i);
                    String stopId = secondJsonObject.getString("stop_id");
                    String arrivalTime = secondJsonObject.getString("arrival_time");
                    arrivalTimesAndStopIds.put(arrivalTime, stopId);
                }

                //Requesting stop names for each stop ID
                List<String> stopNamesList = new ArrayList<>();
                for (String arrivalTime : arrivalTimesAndStopIds.keySet()) {
                    String stopId = arrivalTimesAndStopIds.get(arrivalTime);
                    String apiStopUrl = STOPS_ID_API + stopId;
                    HttpURLConnection stopConn = (HttpURLConnection) new URL(apiStopUrl).openConnection();
                    stopConn.setRequestMethod("GET");

                    BufferedReader stopReader = new BufferedReader(new InputStreamReader(stopConn.getInputStream()));
                    StringBuilder stopResponse = new StringBuilder();
                    String stopLine;
                    while ((stopLine = stopReader.readLine()) != null) {
                        stopResponse.append(stopLine);
                    }
                    stopReader.close();

                    JSONArray stopJsonArray = new JSONArray(stopResponse.toString());
                    if (stopJsonArray.length() > 0) {
                        JSONObject stopJsonObject = stopJsonArray.getJSONObject(0);
                        String stopName = stopJsonObject.getString("stop_name");
                        stopNamesList.add(stopName);
                    }

                    stopConn.disconnect();
                }

                //Creating array of stop names
                String[] stopNamesArray = stopNamesList.toArray(new String[0]);

                //Creating and starting thread for each train
                Train train = new Train(routeId, stopNamesArray);
                Thread thread = new Thread(() -> {
                    train.move();
                });
                threads.add(thread);
                thread.start();

                secondConn.disconnect();
            }

            // Wait for all threads to finish
            for (Thread thread : threads) {
                thread.join();
            }

            conn.disconnect();
        } catch (Exception e) {
            logger.warn("API ERROR");
        }
    }
}
