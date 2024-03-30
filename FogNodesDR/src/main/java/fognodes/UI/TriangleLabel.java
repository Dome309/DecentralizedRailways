package fognodes.UI;

import javax.swing.*;
import java.awt.*;

public class TriangleLabel extends JPanel {
    private Color color;

    TriangleLabel(Color color) {
        this.color = color;
        setPreferredSize(new Dimension(20, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(color);
        int x[] = {0, getWidth() / 2, getWidth()};
        int y[] = {0, getHeight(), 0};
        Polygon p = new Polygon(x, y, 3);
        g2d.fill(p);
        g2d.dispose();
    }
}