package train;

import fognodes.FogNodeMain;

public class TrainMain {
    String apiUrl = "https://www.dati.lombardia.it/resource/yqye-t4rp.json";
    public static void main(String[] args) {

        String[] fogNodes1 = FogNodeMain.readFogNodesFromFile("routes/fog_nodes.txt", "RE5");
        String[] fogNodes2 = FogNodeMain.readFogNodesFromFile("routes/fog_nodes.txt", "RE22");

        Train train1 = new Train("RE5", fogNodes1);
        Train train2 = new Train("RE22", fogNodes2);
        train1.move();
        train2.move();
    }
}
