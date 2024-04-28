package fognodes.UI;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class NodeUI {
    public NodeUI(String label, GeoPosition pos){
        JFrame nodeFrame = new JFrame("Node Information");
        nodeFrame.setSize(300, 450);
        nodeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nodeFrame.setResizable(false);
        nodeFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        ImageIcon imgNode = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/node.png")));
        JLabel labelIcon = new JLabel(imgNode);
        labelIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelInfo = new JLabel(label);
        labelInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelCoordinates = new JLabel(String.valueOf(pos));
        labelCoordinates.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(labelIcon);
        mainPanel.add(labelInfo);
        mainPanel.add(labelCoordinates);

        nodeFrame.add(mainPanel);
        nodeFrame.setVisible(true);
    }
}
