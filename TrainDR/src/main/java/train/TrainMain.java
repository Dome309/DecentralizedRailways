package train;

import fognodes.FogNodeMain;

public class TrainMain {

    public static void main(String[] args) {
        Train train = new Train("RE5", FogNodeMain.fogNodes);
        train.move();
    }
}
