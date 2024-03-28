package train.UI;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

public class TrainWaypointRender implements WaypointRenderer<Waypoint> {
    private static final int CIRCLE_RADIUS = 10;

    @Override
    public void paintWaypoint(Graphics2D graphics, JXMapViewer map, Waypoint waypoint) {
        Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());

        if (waypoint instanceof TrainCustomWaypoint) {
            TrainCustomWaypoint customWaypoint = (TrainCustomWaypoint) waypoint;
            String label = customWaypoint.getLabel();
            Color color = getColorForLabel(label);
            graphics.setColor(color);
            int circleX = (int) point.getX() - CIRCLE_RADIUS;
            int circleY = (int) point.getY() - CIRCLE_RADIUS;
            graphics.fillOval(circleX, circleY, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);

            graphics.setColor(Color.WHITE);
            FontMetrics fm = graphics.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getHeight();
            int startX = circleX + CIRCLE_RADIUS - labelWidth / 2;
            int startY = circleY + CIRCLE_RADIUS + labelHeight / 4;
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
