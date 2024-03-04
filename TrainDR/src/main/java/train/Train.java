package train;

import org.eclipse.paho.client.mqttv3.MqttException;
import train.devices.SpeedControl;
import train.devices.TemperatureControl;


public class Train {
    private String trainID;
    private String[] fogNodes;
    private SpeedControl speedControl;
    private TemperatureControl temperatureControl;
    private int currentLocation;
    private String broker = "tcp://localhost:1883";

    public Train(String name, String[] fogNodes) {
        this.trainID = name;
        this.fogNodes = fogNodes;
        this.currentLocation = 0;
        speedControl = new SpeedControl(broker,"SpeedControl",0);
        temperatureControl = new TemperatureControl(broker, "TemperatureControl", 25);
    }

    public void move() throws MqttException {
        while (currentLocation < fogNodes.length - 1) {
            String currentNode = fogNodes[currentLocation];
            String nextNode = fogNodes[currentLocation + 1];
            System.out.println("Train " + trainID + " has reached node " + currentNode);

            deviceUpdate();
            sendUpdate(currentNode);

            System.out.println("Train " + trainID + " is heading to " + nextNode);

            currentLocation++;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("------------------------------------------------");
        }

        String lastNode = fogNodes[fogNodes.length - 1];
        System.out.println("Train " + trainID + " has arrived at its destination at node " + lastNode);

        deviceUpdate();
        sendUpdate(lastNode);

        speedControl.disconnectDevice();
        temperatureControl.disconnectDevice();
    }

    private void deviceUpdate(){
        speedControl.speedUpdate();
        temperatureControl.temperatureUpdate();
    }

    private void sendUpdate(String currentNode){
        speedControl.sendDataToFogNode(currentNode);
        temperatureControl.sendDataToFogNode(currentNode);
    }
}