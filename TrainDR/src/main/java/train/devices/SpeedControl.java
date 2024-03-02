package train.devices;

public class SpeedControl {
    double trainSpeed;

    public SpeedControl(double startingSpeed){
        this.trainSpeed = startingSpeed;
    }

    public void speedUpdate() {
        trainSpeed += (Math.random() * 10);
        System.out.println("Actual speed: " + String.format("%.2f", trainSpeed) + " km/h");
    }

    public double getTrainSpeed() {
        return trainSpeed;
    }
}
