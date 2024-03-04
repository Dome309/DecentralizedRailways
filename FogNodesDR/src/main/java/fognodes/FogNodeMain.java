package fognodes;

public class FogNodeMain {
    //Train route definition
    public static String[] fogNodes = {"node_A", "node_B", "node_C", "node_D", "node_E"};
    public static void main(String[] args) {
        String broker = "tcp://localhost:1883"; //Mosquitto broker address (local)

        //for each node is created a MQTT client as a thread
        for (String nodeTopic : fogNodes) {
            String clientId = "FogNodeSubscriber_" + nodeTopic.substring(nodeTopic.lastIndexOf('/') + 1);
            new Thread(new FogNodeSubscriber(broker, nodeTopic, clientId)).start();
        }
    }
}
