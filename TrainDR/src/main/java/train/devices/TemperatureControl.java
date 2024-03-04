package train.devices;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class TemperatureControl extends Device{
    double trainTemperature;
    public TemperatureControl(String brokerUrl, String clientId, double startingTemperature){
        super(brokerUrl, clientId);
        this.trainTemperature = startingTemperature;
    }

    @Override
    public void sendDataToFogNode(String node) {
        String message = "Temperature: " + String.format("%.2f", trainTemperature) + " °C";
        System.out.println(clientId+" sending data to..."+node);
        try {
            client.publish(node, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void temperatureUpdate() {
        trainTemperature += Math.random() * 2 - 1;
        System.out.println("Actual temperature: " + String.format("%.2f", trainTemperature) + " °C");
    }

    public double getTrainTemperature() {
        return trainTemperature;
    }

    public void disconnectDevice() throws MqttException {
        client.disconnect();
        System.out.println("TemperatureControl disconnected");
    }
}
