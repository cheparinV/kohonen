package som;

import java.util.List;

public class SomNeuron {

    private List<Double> weights;

    private Double x;

    private Double y;

    private Double error = 0.0;

    public List<Double> getWeights() {
        return weights;
    }

    public SomNeuron setWeights(List<Double> weights) {
        this.weights = weights;
        return this;
    }

    public Double getX() {
        return x;
    }

    public SomNeuron setX(Double x) {
        this.x = x;
        return this;
    }

    public Double getY() {
        return y;
    }

    public SomNeuron setY(Double y) {
        this.y = y;
        return this;
    }

    public Double getError() {
        return error;
    }

    public SomNeuron setError(Double error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "Neuron{" +
                "weights=" + weights +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
