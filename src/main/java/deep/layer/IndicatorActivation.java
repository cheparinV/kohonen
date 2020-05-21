package deep.layer;

import org.nd4j.linalg.activations.BaseActivationFunction;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.gradient.SigmoidDerivative;
import org.nd4j.linalg.api.ops.impl.transforms.strict.Sigmoid;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;

public class IndicatorActivation extends BaseActivationFunction {

    @Override
    public INDArray getActivation(INDArray in, boolean training) {
        Nd4j.getExecutioner().execAndReturn(new Sigmoid(in));
        return in;
    }

    @Override
    public Pair<INDArray, INDArray> backprop(INDArray in, INDArray epsilon) {
        assertShape(in, epsilon);

        Nd4j.getExecutioner().execAndReturn(new SigmoidDerivative(in, epsilon, in));

        return new Pair<>(in, null);
    }
}
