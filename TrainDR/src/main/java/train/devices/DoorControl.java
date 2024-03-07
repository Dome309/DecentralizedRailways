package train.devices;

import org.eclipse.paho.client.mqttv3.MqttException;

public class DoorControl extends Device{
    Boolean doorStatus;
    String doorSubTopic = "door";
    public DoorControl(String brokerUrl, String clientId, boolean doorStatus) {
        super(brokerUrl, clientId);
        this.doorStatus = doorStatus;
    }

    public boolean getStatus(){
        return doorStatus;
    }

    public void doorOpen(){
        System.out.println("Train door is now open...");
        System.out.println("Passengers are getting off the train");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        doorStatus = false;
    }

    public void doorClose(){
        System.out.println("Train door is now closed...");
        doorStatus = true;
    }

    @Override
    public void sendDataToFogNode(String node) {
        String closeOrOpen;
        if (doorStatus == true) {
            closeOrOpen = "closed";
        }else {
            closeOrOpen = "open";
        }
        String message = "Door status: " + closeOrOpen;
        //System.out.println(clientId+" sending data to..."+node);
        try {
            client.publish(mainTopic+node+"/"+doorSubTopic, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
