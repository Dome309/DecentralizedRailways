package train.UI;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

public class TrainWaypointRender implements WaypointRenderer<Waypoint> {
    private static final int SIZE = 14;
    private static final int HALF_SIZE = SIZE / 2;

    @Override
    public void paintWaypoint(Graphics2D graphics, JXMapViewer map, Waypoint waypoint) {
        Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());
        int[] xPoints = {(int) point.getX(), (int) point.getX() - HALF_SIZE, (int) point.getX() + HALF_SIZE};
        int[] yPoints = {(int) point.getY(), (int) point.getY() - SIZE, (int) point.getY() - SIZE};

        if (waypoint instanceof TrainCustomWaypoint) {
            TrainCustomWaypoint customWaypoint = (TrainCustomWaypoint) waypoint;
            String label = customWaypoint.getLabel();
            Color color = getColorForLabel(label);
            graphics.setColor(color);
            graphics.fillPolygon(new Polygon(xPoints, yPoints, 3));
            graphics.setColor(Color.WHITE);
            graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));
            FontMetrics fm = graphics.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getHeight();
            int startX = (int) point.getX() - labelWidth / 2;
            int startY = (int) point.getY() - SIZE - labelHeight / 4;
            graphics.drawString(label, startX - 1, startY);
            graphics.drawString(label, startX + 1, startY);
            graphics.drawString(label, startX, startY - 1);
            graphics.drawString(label, startX, startY + 1);
            graphics.setColor(Color.BLACK);
            graphics.drawString(label, startX, startY);
        }
    }

    private Color getColorForLabel(String label) {
        if (label.startsWith("RE")) {
            return Color.RED;
        } else if (label.startsWith("X")) {
            return Color.ORANGE;
        } else if (label.startsWith("R")) {
            return Color.BLUE;
        } else {
            return Color.GREEN;
        }
    }
}
