package train;

import train.devices.DoorControl;
import train.devices.LightingControl;
import train.devices.SpeedControl;
import train.devices.TemperatureControl;

import static train.devices.Device.numberOfDevices;

public class Train {
    private String trainID;
    private String[] fogNodes; //Train route
    private int currentLocation; //Index for get the current location of the train
    private SpeedControl speedControl; //Speed device declaration
    private TemperatureControl temperatureControl; //Temperature device declaration
    private DoorControl doorControl; //Door control device declaration
    private LightingControl lightingControl; //Light control device declaration
    public Train(String name, String[] fogNodes) {
        this.trainID = name;
        this.fogNodes = fogNodes;
        this.currentLocation = 0;
        speedControl = new SpeedControl("SpeedControl",0);
        temperatureControl = new TemperatureControl("TemperatureControl", 25);
        doorControl = new DoorControl("DoorControl");
        lightingControl = new LightingControl("LightingControl");
    }

    //Method for simulating train movement through the nodes array declared in FogNodeMain
    public void move() {
        connectAllDevices();
            while (currentLocation < fogNodes.length - 1 && doorControl.checkDeviceStatus()) {
                String currentNode = fogNodes[currentLocation];
                String nextNode = fogNodes[currentLocation + 1];
                System.out.println("Train " + trainID + " has reached node " + currentNode);

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
                System.out.println(numberOfDevices);
                System.out.println("------------------------------------------------");
            }
            if(doorControl.checkDeviceStatus()){
                String lastNode = fogNodes[fogNodes.length - 1];
                System.out.println("Train " + trainID + " has arrived at its destination at node " + lastNode);
                doorControl.doorOpen();

                deviceUpdate();
                sendUpdate(lastNode);

                //Close the devices client
                disconnectAllDevices();
            }
    }

    private void deviceUpdate(){
        speedControl.speedUpdate();
        temperatureControl.temperatureUpdate();
        lightingControl.checkStatus();
    }

    private void sendUpdate(String currentNode){
        System.out.println("All devices are sending data to... "+currentNode);
        speedControl.sendDataToFogNode(currentNode);
        temperatureControl.sendDataToFogNode(currentNode);
        doorControl.sendDataToFogNode(currentNode);
        lightingControl.sendDataToFogNode(currentNode);
    }

    private void connectAllDevices() {
        speedControl.connectDevice();
        temperatureControl.connectDevice();
        doorControl.connectDevice();
        lightingControl.connectDevice();
    }

    private void disconnectAllDevices() {
        speedControl.disconnectDevice();
        temperatureControl.disconnectDevice();
        doorControl.disconnectDevice();
        lightingControl.disconnectDevice();
    }
}