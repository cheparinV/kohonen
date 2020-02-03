package som;

import java.util.List;

public class Neuron {

    private List<Double> weights;

    private Double x;

    private Double y;

    public List<Double> getWeights() {
        return weights;
    }

    public Neuron setWeights(List<Double> weights) {
        this.weights = weights;
        return this;
    }

    public Double getX() {
        return x;
    }

    public Neuron setX(Double x) {
        this.x = x;
        return this;
    }

    public Double getY() {
        return y;
    }

    public Neuron setY(Double y) {
        this.y = y;
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
