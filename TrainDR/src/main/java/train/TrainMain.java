package train;

import fognodes.FogNodeMain;

import java.util.Scanner;

public class TrainMain {

    public static void main(String[] args) {

        String[] fogNodes1 = FogNodeMain.readFogNodesFromFile("fog_nodes.txt", "RV2030");

        Train train1 = new Train("RV2030", fogNodes1);
        train1.move();
    }
}
