package train.devices;

import org.eclipse.paho.client.mqttv3.*;

public class SpeedControl extends Device{
    double trainSpeed;

    public SpeedControl(double startingSpeed){
        this.trainSpeed = startingSpeed;
        setupMQTT();
    }

    @Override
    public void sendDataToFogNode(String node) {
        String message = "Speed: " + String.format("%.2f", trainSpeed) + " km/h";
        System.out.println("Sending data to..."+node);
        try {
            client.publish(node, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
}
