package som;

import java.util.List;

public class Row {

    private List<Double> weights;

    private Double classValue;

    public List<Double> getWeights() {
        return weights;
    }

    public Row setWeights(List<Double> weights) {
        this.weights = weights;
        return this;
    }

    public Double getClassValue() {
        return classValue;
    }

    public Row setClassValue(Double classValue) {
        this.classValue = classValue;
        return this;
    }
}
