package train;

import org.eclipse.paho.client.mqttv3.MqttException;
import train.devices.*;

public class Train implements Runnable {
    private final String trainID;
    private final String[] fogNodes; //Train route
    private int currentLocation; //Index for get the current location of the train
    private TrainManager trainManager; //device for communicate TrainID
    private SpeedControl speedControl; //Speed device declaration
    private TemperatureControl temperatureControl; //Temperature device declaration
    private DoorControl doorControl; //Door control device declaration
    private LightingControl lightingControl; //Light control device declaration

    //constructor of the class
    public Train(String name, String[] fogNodes) throws MqttException {
        this.trainID = name;
        this.fogNodes = fogNodes;
        this.currentLocation = 0;
        trainManager = new TrainManager(trainID);
        speedControl = new SpeedControl("SpeedControl" + trainID, 0);
        temperatureControl = new TemperatureControl("TemperatureControl" + trainID, 25);
        doorControl = new DoorControl("DoorControl" + trainID);
        lightingControl = new LightingControl("LightingControl" + trainID);
    }

    //Method for simulating train movement through the nodes array
    public void move() {
        connectAllDevices();
        while (currentLocation < fogNodes.length - 1 && doorControl.checkDeviceStatus()) {
            String currentNode = fogNodes[currentLocation];
            String nextNode = fogNodes[currentLocation + 1];
            System.out.println("Train " + trainID + " has reached node " + currentNode + " " + trainManager.printStationCoordinates(currentNode));

            //Update data monitored by each device and sending them to the current node
            deviceUpdate();
            sendUpdate(currentNode);

            doorControl.doorOpen();

            doorControl.doorClose();

            System.out.println("Train " + trainID + " is heading to " + nextNode);
            currentLocation++;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("------------------------------------------------");
        }
        if (doorControl.checkDeviceStatus()) {
            String lastNode = fogNodes[fogNodes.length - 1];
            System.out.println("Train " + trainID + " has arrived at its destination at node " + lastNode);
            doorControl.doorOpen();

            deviceUpdate();
            sendUpdate(lastNode);

            //Close the devices client
            disconnectAllDevices();
        }
    }

    //method for update the values of devices
    private void deviceUpdate() {
        speedControl.speedUpdate();
        temperatureControl.temperatureUpdate();
        lightingControl.checkStatus();
    }

    //method for sending the data acquired to the current node
    private void sendUpdate(String currentNode) {
        System.out.println("All devices are sending data to... " + currentNode);
        trainManager.sendDataToFogNode(currentNode);
        speedControl.sendDataToFogNode(currentNode);
        temperatureControl.sendDataToFogNode(currentNode);
        doorControl.sendDataToFogNode(currentNode);
        lightingControl.sendDataToFogNode(currentNode);
    }

    //method for connecting every device on board to MQTT broker
    private void connectAllDevices() {
        trainManager.connectDevice();
        speedControl.connectDevice();
        temperatureControl.connectDevice();
        doorControl.connectDevice();
        lightingControl.connectDevice();
    }

    //method for disconnecting every device on board to MQTT broker
    private void disconnectAllDevices() {
        trainManager.disconnectDevice();
        speedControl.disconnectDevice();
        temperatureControl.disconnectDevice();
        doorControl.disconnectDevice();
        lightingControl.disconnectDevice();
    }

    @Override
    public void run() {
        move();
    }
}