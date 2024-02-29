package train;

import fognodes.FogNodeMain;
import org.eclipse.paho.client.mqttv3.MqttException;

public class TrainMain {

    public static void main(String[] args) throws MqttException {
        Train train = new Train("RE5", FogNodeMain.fogNodes);
        train.move();
    }
}
