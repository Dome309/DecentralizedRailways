package fognodes;

public class FogNodeMain {
    public static String[] fogNodes = {"node_A", "node_B", "node_C", "node_D", "node_E"};
    public static void main(String[] args) {
        String broker = "tcp://localhost:1883";

        for (String nodeTopic : fogNodes) {
            String clientId = "FogNodeSubscriber_" + nodeTopic.substring(nodeTopic.lastIndexOf('/') + 1);
            new Thread(new FogNodeSubscriber(broker, nodeTopic, clientId)).start();
        }
    }
}
