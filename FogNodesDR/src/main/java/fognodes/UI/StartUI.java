package fognodes.UI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fognodes.FogNodeMain;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class StartUI {
    private final static String apiUrl = "https://www.dati.lombardia.it/resource/j5jz-kvqn.json";

    public StartUI(){
        //Creating the frame
        JFrame frame = new JFrame("RailwayMap");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create a JXMapKit for viewing the map
        JXMapKit mapKit = new JXMapKit();
        frame.add(mapKit, BorderLayout.CENTER);

        //Setting the default location and zoom level
        mapKit.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        GeoPosition center = new GeoPosition(45.484462, 9.187875);
        mapKit.setAddressLocation(center);
        mapKit.setZoom(10);

        //Create a set of waypoints
        Set<DefaultWaypoint> waypoints = new HashSet<>();

        try {
            //Fetch JSON data from the provided URL
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new URL(apiUrl));

            //Add waypoints to the set
            for (JsonNode node : jsonNode) {
                double latitude = node.get("stop_lat").asDouble();
                double longitude = node.get("stop_lon").asDouble();
                DefaultWaypoint waypoint = new DefaultWaypoint(new GeoPosition(latitude, longitude));
                waypoints.add(waypoint);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create a WaypointPainter to manage the waypoints on the map
        WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);

        //Set a custom waypoint renderer
        waypointPainter.setRenderer(new WaypointRender());

        //Add the WaypointPainter to the map
        mapKit.getMainMap().setOverlayPainter(waypointPainter);

        //Display the JFrame and start the backend activity
        frame.setVisible(true);
        FogNodeMain.StartNetwork(apiUrl);
    }
}
