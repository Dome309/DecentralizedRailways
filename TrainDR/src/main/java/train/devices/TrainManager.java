package train.devices;

import org.eclipse.paho.client.mqttv3.MqttException;

public class TrainManager extends Device {
    private String trainManagerSubTopic = "trainManager";
    public TrainManager(String clientId) {
        super(clientId);
    }

    @Override
    public void sendDataToFogNode(String node) {
        String message = "Train: " + clientId;
        try {
            client.publish(mainTopic+node+"/"+trainManagerSubTopic, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
