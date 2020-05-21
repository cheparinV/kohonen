package smile.layer;

import smile.base.mlp.ActivationFunction;
import smile.base.mlp.HiddenLayer;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.Matrix;

import java.util.Arrays;

public class CustomHiddenLayer extends HiddenLayer {

    protected final double[] indicator;

    /**
     * Constructor.
     *
     * @param n the number of neurons.
     * @param p the number of input variables (not including bias value).
     * @param f the activation function.
     */
    public CustomHiddenLayer(int n, int p, ActivationFunction f) {
        super(n, p, f);
        this.indicator = new double[p];
        Arrays.fill(this.indicator, 0.5);
    }

    public CustomHiddenLayer(int n, int p, ActivationFunction f, double[] indicators) {
        super(n, p, f);
        this.indicator = indicators;
    }

    @Override
    public void propagate(double[] x) {
        System.arraycopy(bias, 0, output, 0, n);
        final DenseMatrix indicator = indicator(x);
        DenseMatrix mul = weight.mul(indicator);
        mul.axpy(x, output);
        f(output);
    }

    private DenseMatrix indicator(double[] x) {
        final DenseMatrix zeros = Matrix.zeros(n, p);
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < x.length; j++) {
                double div = Math.floor(x[j] / indicator[j]);
                double mod = i % 21;
                double val = div == mod ? 1.0 : 0.0;
                zeros.set(i, j, val);
            }
        }
        return zeros;
    }
}
