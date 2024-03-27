package fognodes.UI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class StartUI {
    public final static String apiUrl = "https://www.dati.lombardia.it/resource/j5jz-kvqn.json";
    public static JFrame frame = new JFrame("RailwayMap");
    public static JXMapKit mapKit = new JXMapKit(); //Create a JXMapKit for viewing the map
    private static Set<DefaultWaypoint> waypointsTrain = new HashSet<>();
    private static WaypointPainter<Waypoint> trainWaypointPainter = new WaypointPainter<>();
    private static CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>();

    public void startMap(){
        //Creating the frame
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(mapKit, BorderLayout.CENTER);

        //Set the default location and zoom level
        mapKit.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        GeoPosition center = new GeoPosition(45.816444, 8.832364); // Coordinates provided
        mapKit.setAddressLocation(center);
        mapKit.setZoom(10);

        //Create a set of waypoints
        Set<DefaultWaypoint> waypoints = new HashSet<>();

        try {
            //Fetch JSON data from the provided URL
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new URL(apiUrl));

            //Parse JSON data and add waypoints to the set
            for (JsonNode node : jsonNode) {
                double latitude = node.get("stop_lat").asDouble();
                double longitude = node.get("stop_lon").asDouble();
                DefaultWaypoint waypoint = new DefaultWaypoint(new GeoPosition(latitude, longitude));
                waypoints.add(waypoint);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create a WaypointPainter to paint the station waypoints on the map
        WaypointPainter<DefaultWaypoint> stationWaypointPainter = new WaypointPainter<>();
        stationWaypointPainter.setWaypoints(waypoints);
        stationWaypointPainter.setRenderer(new WaypointRender());

        //Create a CompoundPainter to combine both painters
        compoundPainter.addPainter(stationWaypointPainter);

        //Add the CompoundPainter to the map
        mapKit.getMainMap().setOverlayPainter(compoundPainter);

        //Display the JFrame
        frame.setVisible(true);
    }

    public void addWaypoints(DefaultWaypoint newWaypoint) {
        waypointsTrain.add(newWaypoint);
        trainWaypointPainter.setWaypoints(waypointsTrain);

        compoundPainter.removePainter(trainWaypointPainter);
        compoundPainter.addPainter(trainWaypointPainter);

        mapKit.repaint();
    }
}
