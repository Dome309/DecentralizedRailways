package train.devices;

import org.eclipse.paho.client.mqttv3.*;

public class SpeedControl extends Device{
    double trainSpeed;
    String speedSubTopic = "speed";
    String clientId = "speed";
    public SpeedControl(String clientId , double startingSpeed){
        super(clientId);
        this.trainSpeed = startingSpeed;
    }

    public void speedUpdate() {
        trainSpeed += (Math.random() * 10);
        System.out.println("Actual speed: " + String.format("%.2f", trainSpeed) + " km/h");
    }

    public double getTrainSpeed() {
        return trainSpeed;
    }

    @Override
    public void sendDataToFogNode(String node) {
        String message = "Speed: " + String.format("%.2f", trainSpeed) + " km/h";
        //System.out.println(clientId+" sending data to..."+node);
        try {
            client.publish(mainTopic+node+"/"+speedSubTopic, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
