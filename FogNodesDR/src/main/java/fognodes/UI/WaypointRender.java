package fognodes.UI;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Ellipse2D;


public class WaypointRender implements WaypointRenderer<DefaultWaypoint> {

    //draws node waypoints
    @Override
    public void paintWaypoint(Graphics2D graphics, JXMapViewer map, DefaultWaypoint waypoint) {
        graphics.setColor(Color.decode("#006400"));
        int size = 7;
        double x = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom()).getX();
        double y = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom()).getY();
        graphics.fill(new Ellipse2D.Double(x, y, size, size));
    }
}
