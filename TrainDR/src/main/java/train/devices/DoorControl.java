package train.devices;

import org.eclipse.paho.client.mqttv3.MqttException;

public class DoorControl extends Device{
    Boolean doorStatus;

    public DoorControl(String brokerUrl, String clientId, boolean doorStatus) {
        super(brokerUrl, clientId);
        this.doorStatus = doorStatus;
    }

    public boolean getStatus(){
        return doorStatus;
    }

    public void doorOpen(){
        System.out.println("Train door is now open..");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        doorStatus = false;
    }

    public void doorClose(){
        System.out.println("Train door is now closed..");
        doorStatus = true;
    }

    @Override
    public void sendDataToFogNode(String node) {
        String message = "Door status: " + doorStatus;
        System.out.println(clientId+" sending data to..."+node);
        try {
            client.publish(node, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
