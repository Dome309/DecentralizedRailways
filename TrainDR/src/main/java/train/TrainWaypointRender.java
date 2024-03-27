package train;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class TrainWaypointRender implements WaypointRenderer<Waypoint> {
    private static final int SIZE = 8;

    @Override
    public void paintWaypoint(Graphics2D graphics, JXMapViewer map, Waypoint waypoint) {
        graphics.setColor(Color.BLUE);
        Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());
        double x = point.getX();
        double y = point.getY();
        graphics.fill(new Ellipse2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE));
    }
}
