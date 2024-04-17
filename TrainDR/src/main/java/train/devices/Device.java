package train.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(Device.class);

    public Device(String clientId) {
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
            logger.warn("{} creation failed", clientId);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.warn("{} connection lost: {}", clientId, cause.getMessage());
    }

    @Override
    public void messageArrived(String node, MqttMessage mqttMessage) {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public abstract void sendDataToFogNode(String node);

    public void connectDevice() {
        this.deviceStatus = true;
        logger.info("{} is activated", clientId);
    }

    public void disconnectDevice() {
        this.deviceStatus = false;
        logger.info("{} is deactivated ", clientId);
    }

    public boolean checkDeviceStatus() {
        return deviceStatus;
    }
}
