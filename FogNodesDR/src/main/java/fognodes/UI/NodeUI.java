package fognodes.UI;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class NodeUI {

    public NodeUI(String label, GeoPosition pos){
        JFrame nodeFrame = createNodeFrame();
        JPanel infoPanel = createInfoPanel(label);
        JPanel coordinatePanel = createCoordinatePanel(pos);

        nodeFrame.add(infoPanel);
        nodeFrame.add(coordinatePanel);
        nodeFrame.setVisible(true);
    }

    private JFrame createNodeFrame() {
        JFrame nodeFrame = new JFrame("Node Information");
        nodeFrame.setSize(300, 450);
        nodeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nodeFrame.setLayout(new GridLayout(3, 1));
        nodeFrame.setResizable(false);
        nodeFrame.setLocationRelativeTo(null);
        return nodeFrame;
    }

    private JPanel createInfoPanel(String label) {
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

        return infoPanel;
    }

    private JPanel createCoordinatePanel(GeoPosition pos) {
        JPanel coordinatePanel = new JPanel(new GridLayout(1, 2));

        coordinatePanel.add(createCoordinateField("Latitude:", Double.toString(pos.getLatitude())));
        coordinatePanel.add(createCoordinateField("Longitude:", Double.toString(pos.getLongitude())));

        return coordinatePanel;
    }

    private JPanel createCoordinateField(String label, String value) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel labelField = new JLabel(label);
        fieldPanel.add(labelField);

        JTextField textField = new JTextField(value, 10);
        textField.setEditable(false);
        fieldPanel.add(textField);

        return fieldPanel;
    }
}
