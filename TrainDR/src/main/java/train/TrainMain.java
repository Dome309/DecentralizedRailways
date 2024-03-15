package train;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TrainMain {
    private static final Logger logger = LogManager.getLogger(TrainMain.class);
    String apiUrl = "https://www.dati.lombardia.it/resource/yqye-t4rp.json";
    public static void main(String[] args) {

        String[] fogNodes1 = readFogNodesFromFile("routes/fog_nodes.txt", "RE5");
        //String[] fogNodes2 = FogNodeMain.readFogNodesFromFile("routes/fog_nodes.txt", "RE22");

        Train train1 = new Train("RE5", fogNodes1);
        //Train train2 = new Train("RE22", fogNodes2);
        train1.move();
        //train2.move();
    }

    public static String[] readFogNodesFromFile(String filename, String routes) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].trim().equals(routes)) {
                    String[] nodes = parts[1].trim().split(", ");
                    return nodes;
                }
            }
        } catch (IOException e) {
            logger.error("Error reading fog nodes from file", e);
        }
        return new String[0];
    }
}
