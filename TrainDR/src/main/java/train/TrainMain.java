package train;

import fognodes.FogNodeMain;

import java.util.Scanner;

public class TrainMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Insert trainId: ");
        String trainId = scanner.nextLine();

        String[] fogNodes = FogNodeMain.readFogNodesFromFile("fog_nodes.txt", trainId);

        Train train = new Train(trainId, fogNodes);
        train.move();
    }
}
