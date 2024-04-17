package train.UI;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class TrainCustomWaypoint extends DefaultWaypoint {
    private final String label;

    public TrainCustomWaypoint(double latitude, double longitude, String label) {
        super(new GeoPosition(latitude, longitude));
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
