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
    private static final String TRIP_ID_API = "https://www.dati.lombardia.it/resource/4z9q-hrcb.json?trip_id=";

    public static void main(String[] args) {
        try {
            JSONArray jsonArray = fetchDataFromApi(ROUTES_ID_API);
            Map<String, String> routes = extractRoutes(jsonArray);
            List<Thread> trainThreads = createAndStartThreads(routes);
            waitAllThreads(trainThreads);
        } catch (Exception e) {
            logger.warn("API ERROR");
        }
    }

    private static JSONArray fetchDataFromApi(String apiUrl) throws Exception {
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

    private static Map<String, String> extractRoutes(JSONArray jsonArray) {
        Map<String, String> routes = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String routeId = jsonObject.getString("route_id");
            String tripId = jsonObject.getString("trip_id");

            if (!routes.containsKey(routeId)) {
                routes.put(routeId, tripId);
            }
        }
        return routes;
    }

    private static List<Thread> createAndStartThreads(Map<String, String> routes) throws Exception {
        List<Thread> threads = new ArrayList<>();
        for (Map.Entry<String, String> entry : routes.entrySet()) {
            String routeId = entry.getKey();
            String tripId = entry.getValue();

            JSONArray secondJsonArray = fetchDataFromApi(TRIP_ID_API + tripId);
            Map<String, String> arrivalTimesAndStopIds = extractArrivalTimesAndStopIds(secondJsonArray);
            String[] stopNamesArray = fetchStopNames(arrivalTimesAndStopIds);

            Train train = new Train(routeId, stopNamesArray);
            Thread thread = new Thread(train);
            threads.add(thread);
            thread.start();
        }
        return threads;
    }

    private static Map<String, String> extractArrivalTimesAndStopIds(JSONArray jsonArray) {
        Map<String, String> arrivalTimesAndStopIds = new TreeMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject secondJsonObject = jsonArray.getJSONObject(i);
            String stopId = secondJsonObject.getString("stop_id");
            String arrivalTime = secondJsonObject.getString("arrival_time");
            arrivalTimesAndStopIds.put(arrivalTime, stopId);
        }
        return arrivalTimesAndStopIds;
    }

    private static String[] fetchStopNames(Map<String, String> arrivalTimesAndStopIds) throws Exception {
        List<String> stopNamesList = new ArrayList<>();
        for (String arrivalTime : arrivalTimesAndStopIds.keySet()) {
            String stopId = arrivalTimesAndStopIds.get(arrivalTime);
            String apiStopUrl = STOPS_ID_API + stopId;
            JSONArray stopJsonArray = fetchDataFromApi(apiStopUrl);
            if (!stopJsonArray.isEmpty()) {
                JSONObject stopJsonObject = stopJsonArray.getJSONObject(0);
                String stopName = stopJsonObject.getString("stop_name");
                stopNamesList.add(stopName);
            }
        }
        return stopNamesList.toArray(new String[0]);
    }

    private static void waitAllThreads(List<Thread> threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
