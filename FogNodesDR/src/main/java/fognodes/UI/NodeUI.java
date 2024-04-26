package fognodes.UI;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;

public class NodeUI {
    public NodeUI(String label, GeoPosition pos){
        JFrame nodeFrame = new JFrame("Node Information");
        nodeFrame.setSize(400, 550);
        nodeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nodeFrame.setLocationRelativeTo(null);
        nodeFrame.setLayout(null);

        JLabel labelInfo = new JLabel(label);
        labelInfo.setBounds(0, 0, 200, 20);

        JLabel labelCoordinates = new JLabel(String.valueOf(pos));
        labelCoordinates.setBounds(0, 50, 200, 20);

        nodeFrame.add(labelInfo);
        nodeFrame.add(labelCoordinates);
        nodeFrame.setVisible(true);
    }

    private void addTrainCounter(){

    }
}
