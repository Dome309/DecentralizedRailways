package train.devices;

import org.eclipse.paho.client.mqttv3.*;

public class SpeedControl extends Device{
    double trainSpeed;
    public SpeedControl(String brokerUrl, String clientId , double startingSpeed){
        super(brokerUrl, clientId);
        this.trainSpeed = startingSpeed;
    }

    public void speedUpdate() {
        trainSpeed += (Math.random() * 10);
        System.out.println("Actual speed: " + String.format("%.2f", trainSpeed) + " km/h");
    }

    public double getTrainSpeed() {
        return trainSpeed;
    }


    public void disconnectDevice() throws MqttException {
        client.disconnect();
        System.out.println("SpeedControl disconnected");
    }

    @Override
    public void sendDataToFogNode(String node) {
        String message = "Speed: " + String.format("%.2f", trainSpeed) + " km/h";
        System.out.println(clientId+" sending data to..."+node);
        try {
            client.publish(node, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
