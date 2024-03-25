package train;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class TrainWaypointRender implements WaypointRenderer<DefaultWaypoint> {
    private int size;
    private double x;
    private double y;

    @Override
    public void paintWaypoint(Graphics2D graphics, JXMapViewer map, DefaultWaypoint waypoint) {
        graphics.setColor(Color.BLUE);
        size = 22;
        x = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom()).getX();
        y = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom()).getY();
        graphics.fill(new Ellipse2D.Double(x, y, size, size));
    }
}