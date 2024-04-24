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
import train.UI.TrainCustomWaypoint;
import train.UI.TrainWaypointRender;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StartUI {
    public final static String apiUrl = "https://www.dati.lombardia.it/resource/j5jz-kvqn.json";
    public static JFrame frame = new JFrame("RailwayMap");
    public static JXMapKit mapKit = new JXMapKit(); //Create a JXMapKit for viewing the map
    private static Set<DefaultWaypoint> waypointsTrain = new HashSet<>();
    private static WaypointPainter<Waypoint> trainWaypointPainter = new WaypointPainter<>();
    private static CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>();
    private static JTextPane errorTextArea = new JTextPane();
    public static StyledDocument errorTextAreaDocument = errorTextArea.getStyledDocument();
    private static double TOLERANCE = 0.2;

    public void startMap() {
        //Creating the frame
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(mapKit, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(800, 600));

        //Set the default location and zoom level
        mapKit.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        GeoPosition center = new GeoPosition(45.485188, 9.202954);
        mapKit.setAddressLocation(center);
        mapKit.setZoom(10);

        createUILegendComponents();

        //Create a set of waypoints
        Set<DefaultWaypoint> waypoints = new HashSet<>();
        Map<DefaultWaypoint, String> waypointLabels = new HashMap<>();

        try {
            //Fetch JSON data from the provided URL
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new URL(apiUrl));

            //Parse JSON data and add waypoints to the set
            for (JsonNode node : jsonNode) {
                double latitude = node.get("stop_lat").asDouble();
                double longitude = node.get("stop_lon").asDouble();
                String stopName = node.get("stop_name").asText();
                DefaultWaypoint waypoint = new DefaultWaypoint(new GeoPosition(latitude, longitude));
                waypoints.add(waypoint);
                waypointLabels.put(waypoint, stopName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapKit.getMainMap().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point2D point = e.getPoint();
                GeoPosition position = mapKit.getMainMap().convertPointToGeoPosition(point);

                for (Waypoint waypoint : waypoints) {
                    GeoPosition wpPos = waypoint.getPosition();
                    double distance = calculateDistance(position.getLatitude(), position.getLongitude(), wpPos.getLatitude(), wpPos.getLongitude());
                    if (distance < TOLERANCE) {
                        String label = waypointLabels.get(waypoint);
                        new NodeUI(label);
                    }
                }
            }
        });

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

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double APPROX_DEGREE_LENGTH = 111.12;
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double distance = Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon) * APPROX_DEGREE_LENGTH;

        return distance;
    }

    public void addWaypoints(TrainCustomWaypoint newWaypoint) {
        waypointsTrain.add(newWaypoint);
        trainWaypointPainter.setWaypoints(waypointsTrain);
        trainWaypointPainter.setRenderer(new TrainWaypointRender());
        compoundPainter.removePainter(trainWaypointPainter);
        compoundPainter.addPainter(trainWaypointPainter);

        mapKit.repaint();
    }

    //method for creating
    private void createUILegendComponents() {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(null);
        legendPanel.setPreferredSize(new Dimension(420, 720));

        JLabel legendTitle = new JLabel("Trenord Lines");
        legendTitle.setBounds(135, 25, 200, 30);
        legendTitle.setFont(new Font("Arial", Font.BOLD, 24));

        errorTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(errorTextArea);
        scrollPane.setBounds(10, 275, 400, 300);

        JPanel trianglePanel1 = new TriangleLabel(Color.RED);
        trianglePanel1.setBounds(30, 88, 20, 20);

        JPanel trianglePanel2 = new TriangleLabel(Color.BLUE);
        trianglePanel2.setBounds(255, 88, 20, 20);

        JPanel trianglePanel3 = new TriangleLabel(Color.ORANGE);
        trianglePanel3.setBounds(30, 165, 20, 20);

        JPanel trianglePanel4 = new TriangleLabel(Color.GREEN);
        trianglePanel4.setBounds(255, 165, 20, 20);

        JLabel label1 = new JLabel("R. Express");
        label1.setBounds(73, 88, 100, 20);
        label1.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel label2 = new JLabel("Regionals");
        label2.setBounds(298, 88, 100, 20);
        label2.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel label3 = new JLabel("M. Express");
        label3.setBounds(73, 165, 100, 20);
        label3.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel label4 = new JLabel("Suburban");
        label4.setBounds(298, 165, 100, 20);
        label4.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel label5 = new JLabel("Warning logs");
        label5.setBounds(136, 240, 200, 30);
        label5.setFont(new Font("Arial", Font.BOLD, 24));

        legendPanel.add(legendTitle);
        legendPanel.add(label1);
        legendPanel.add(label2);
        legendPanel.add(label3);
        legendPanel.add(label4);
        legendPanel.add(label5);
        legendPanel.add(trianglePanel1);
        legendPanel.add(trianglePanel2);
        legendPanel.add(trianglePanel3);
        legendPanel.add(trianglePanel4);
        legendPanel.add(scrollPane);
        frame.add(legendPanel, BorderLayout.EAST);
    }
}
