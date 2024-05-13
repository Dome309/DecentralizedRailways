package train.devices;

import org.eclipse.paho.client.mqttv3.MqttException;

public class DoorControl extends Device {
    String doorSubTopic = "door";

    public DoorControl(String clientId) {
        super(clientId);
    }

    public void doorOpen() {
        System.out.println("Train door is now open...");
        System.out.println("Passengers are getting off the train");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        deviceStatus = false;
    }

    public void doorClose() {
        System.out.println("Train door is now closed...");
        deviceStatus = true;
    }

    @Override
    public void sendDataToFogNode(String node) {
        String closeOrOpen;
        if (deviceStatus) {
            closeOrOpen = "closed";
        } else {
            closeOrOpen = "open";
        }
        String message = "Door status: " + closeOrOpen;
        try {
            client.publish(mainTopic + node + "/" + doorSubTopic, message.getBytes(), 2, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
