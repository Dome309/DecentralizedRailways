package fognodes.UI;

import javax.swing.*;
import java.awt.*;

public class NodeUI {
    public NodeUI(String label){
        JFrame infoFrame = new JFrame("Node Information");
        infoFrame.setSize(300, 200);
        infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel labelInfo = new JLabel(label);
        panel.add(labelInfo, BorderLayout.CENTER);

        infoFrame.add(panel);
        infoFrame.setVisible(true);
    }

    private void addTrainCounter(){

    }
}
