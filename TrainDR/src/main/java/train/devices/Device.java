package train.devices;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public abstract class Device implements MqttCallback {
    protected MqttClient client;
    protected String brokerUrl;
    protected String clientId;
    protected MemoryPersistence persistence;
    protected String mainTopic = "devices/";
    protected boolean deviceStatus;
    public static int numberOfDevices = 0;
    public Device(String clientId){
        this.deviceStatus = false;
        this.brokerUrl = "tcp://localhost:1883";
        this.clientId = clientId;
        this.persistence = new MemoryPersistence();
        numberOfDevices++;
        try {
            client = new MqttClient(brokerUrl, clientId, persistence);
            client.setCallback(this);
            client.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println(clientId+" connection lost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String node, MqttMessage message) throws Exception {
        //actually device doesn't receive any message
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //System.out.println(clientId+ " delivery completed: "+token.isComplete());
    }

    public abstract void sendDataToFogNode(String node);

    public void connectDevice(){
        this.deviceStatus = true;
        System.out.println(clientId+" is activated ");
    }

    public void disconnectDevice(){
        this.deviceStatus = false;
        System.out.println(clientId+" is deactivated ");
    }

    public boolean checkDeviceStatus(){
        return deviceStatus;
    }
}
