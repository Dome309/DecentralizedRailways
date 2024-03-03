package train.devices;

public class TemperatureControl {
    double trainTemperature;
    private String[] fogNodes;

    public TemperatureControl(double startingTemperature, String[] fogNodes){
        this.trainTemperature = startingTemperature;
    }

    public void temperatureUpdate() {
        trainTemperature += Math.random() * 2 - 1;
        System.out.println("Actual temperature: " + String.format("%.2f", trainTemperature) + " °C");
    }

    public double getTrainTemperature() {
        return trainTemperature;
    }
}
