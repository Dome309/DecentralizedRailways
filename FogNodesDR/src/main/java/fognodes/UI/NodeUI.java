package fognodes.UI;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class NodeUI {
    public NodeUI(String label, GeoPosition pos){
        JFrame nodeFrame = new JFrame("Node Information");
        nodeFrame.setSize(300, 450);
        nodeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nodeFrame.setLayout(new GridLayout(2, 1));
        nodeFrame.setResizable(false);
        nodeFrame.setLocationRelativeTo(null);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        ImageIcon imgNode = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/node.png")));
        JLabel labelIcon = new JLabel(imgNode);
        labelIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(labelIcon);
        
        JLabel labelInfo = new JLabel(label);
        labelInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(labelInfo);

        JPanel coordinatePanel = getjPanel(pos);

        nodeFrame.add(infoPanel);
        nodeFrame.add(coordinatePanel);
        nodeFrame.setVisible(true);
    }

    private JPanel getjPanel(GeoPosition pos) {
        JPanel coordinatePanel = new JPanel(new GridLayout(1, 2));

        double latitude = pos.getLatitude();
        double longitude = pos.getLongitude();

        JLabel labelLatitude = new JLabel("Latitude: " + latitude);
        coordinatePanel.add(labelLatitude);

        JLabel labelLongitude = new JLabel("Longitude: " + longitude);
        coordinatePanel.add(labelLongitude);
        return coordinatePanel;
    }
}
