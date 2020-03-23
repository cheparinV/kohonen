package som;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SOM {

    private List<SomNeuron> neurons;

    private int weightSize;

    private int length;

    private int width;

    public SOM(int weightSize, int length, int width) {
        this.weightSize = weightSize;
        this.length = length;
        this.width = width;
    }

    public void generateNeurons(List<List<Double>> rows) {
        
    }

    public void generateNeurons() {
        double distance = 5.0; // расстояние между нейронами
        neurons = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                neurons.add(
                        createNeuron().setX(j * distance)
                                      .setY(i * distance));
            }
        }
    }

    private SomNeuron createNeuron() {
        final RandomDataGenerator generator = new RandomDataGenerator();
        final ArrayList<Double> weights = new ArrayList<>(weightSize);
        for (int i = 0; i < weightSize; i++) {
            weights.add(generator.nextUniform(0.0, 1.0));
        }

        return new SomNeuron().setWeights(weights);
    }

    public List<SomNeuron> getNeurons() {
        return neurons;
    }

    public int getWeightSize() {
        return weightSize;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return neurons.stream()
                      .map(SomNeuron::toString)
                      .collect(Collectors.joining("\n"));
    }
}
