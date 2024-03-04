package train.devices;

import org.eclipse.paho.client.mqttv3.MqttException;

public class LightingControl extends Device{
    boolean lightStatus;

    public LightingControl(String brokerUrl, String clientId, boolean lightStatus) {
        super(brokerUrl, clientId);
        this.lightStatus = lightStatus;
    }

    public void lightOn(){
        System.out.println("Lights switched on");
        lightStatus = true;
    }

    public void lightOff(){
        System.out.println("Lights switched off");
        lightStatus = false;
    }

    public void checkStatus(){
        if(lightStatus){
            System.out.println("Lights are on");
        }else{
            System.out.println("Lights are off");
        }
    }

    @Override
    public void sendDataToFogNode(String node) {
        String lightOnOff;
        if (lightStatus == true) {
            lightOnOff = "on";
        }else {
            lightOnOff = "off";
        }
        String message = "Light status: " + lightOnOff;
        //System.out.println(clientId+" sending data to..."+node);
        try {
            client.publish(node, message.getBytes(), 1, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
