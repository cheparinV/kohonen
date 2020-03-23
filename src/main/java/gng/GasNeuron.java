package gng;

import java.util.List;
import java.util.UUID;

public class GasNeuron {

    private final UUID key;

    private List<Double> weights;

    private Double error = 0.0;

    public GasNeuron() {
        this.key = UUID.randomUUID();
    }

    public List<Double> getWeights() {
        return weights;
    }

    public GasNeuron setWeights(List<Double> weights) {
        this.weights = weights;
        return this;
    }

    public Double getError() {
        return error;
    }

    public GasNeuron setError(Double error) {
        this.error = error;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GasNeuron gasNeuron = (GasNeuron) o;

        return key != null ? key.equals(gasNeuron.key) : gasNeuron.key == null;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GasNeuron{" +
                "weights=" + weights +
                '}';
    }
}
