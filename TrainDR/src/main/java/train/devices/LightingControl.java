package train.devices;

import org.eclipse.paho.client.mqttv3.MqttException;

public class LightingControl extends Device {
    String lightSubTopic = "light";

    public LightingControl(String clientId) {
        super(clientId);
    }

    public void checkStatus() {
        if (deviceStatus) {
            System.out.println("Lights are on");
        } else {
            System.out.println("Lights are off");
        }
    }

    @Override
    public void sendDataToFogNode(String node) {
        String lightOnOff;
        if (deviceStatus) {
            lightOnOff = "on";
        } else {
            lightOnOff = "off";
        }
        String message = "Light status: " + lightOnOff;
        try {
            client.publish(mainTopic + node + "/" + lightSubTopic, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
