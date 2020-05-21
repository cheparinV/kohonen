package smile.layer;

import smile.base.mlp.ActivationFunction;
import smile.base.mlp.Layer;
import smile.base.mlp.LayerBuilder;

public class CustomLayerBuilder extends LayerBuilder {

    /** The activation function. */
    private ActivationFunction f;

    private double[] indicators;

    /**
     * Constructor.
     *
     * @param n the number of neurons.
     */
    public CustomLayerBuilder(int n, ActivationFunction f) {
        super(n);
        this.f = f;
    }

    public CustomLayerBuilder(int n, ActivationFunction f, double[] indicators) {
        super(n);
        this.f = f;
        this.indicators = indicators;
    }

    @Override
    public Layer build(int p) {
        if (indicators == null) {
            return new CustomHiddenLayer(n, p, f);
        }
        return new CustomHiddenLayer(n, p, f, indicators);
    }

    public static LayerBuilder sigmoid(int n) {
        return new CustomLayerBuilder(n, ActivationFunction.sigmoid());
    }

    public static LayerBuilder sigmoid(int n, double[] indicators) {
        return new CustomLayerBuilder(n, ActivationFunction.sigmoid(), indicators);
    }
}
