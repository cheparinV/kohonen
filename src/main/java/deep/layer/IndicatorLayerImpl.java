package deep.layer;

import org.deeplearning4j.exception.DL4JInvalidInputException;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.layers.BaseLayer;
import org.deeplearning4j.nn.params.DefaultParamInitializer;
import org.deeplearning4j.nn.workspace.ArrayType;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.custom.LayerNorm;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class IndicatorLayerImpl extends BaseLayer<IndicatorLayer> {

    private double[] indWeight;

    private INDArray zeros;

    private long nOut;

    public IndicatorLayerImpl(NeuralNetConfiguration conf, DataType dataType) {
        super(conf, dataType);
    }

    @Override
    public INDArray activate(boolean training, LayerWorkspaceMgr workspaceMgr) {
        return super.activate(training, workspaceMgr);
    }

    @Override
    protected INDArray preOutput(boolean training, LayerWorkspaceMgr workspaceMgr) {
        return preOutputWithPreNorm(training, false, workspaceMgr).getFirst();
    }

    @Override
    protected Pair<INDArray, INDArray> preOutputWithPreNorm(boolean training, boolean forBackprop,
                                                            LayerWorkspaceMgr workspaceMgr) {
        assertInputSet(forBackprop);
        applyDropOutIfNecessary(training, workspaceMgr);
        INDArray W = getParamWithNoise(DefaultParamInitializer.WEIGHT_KEY, training, workspaceMgr);
        INDArray b = getParamWithNoise(DefaultParamInitializer.BIAS_KEY, training, workspaceMgr);
        INDArray g = (hasLayerNorm() ? getParam(DefaultParamInitializer.GAIN_KEY) : null);

        INDArray input = this.input.castTo(dataType);

        //Input validation:
        if (input.rank() != 2 || input.columns() != W.rows()) {
            if (input.rank() != 2) {
                throw new DL4JInvalidInputException("Input that is not a matrix; expected matrix (rank 2), got rank "
                        + input.rank() + " array with shape " + Arrays.toString(input.shape())
                        + ". Missing preprocessor or wrong input type? " + layerId());
            }
            throw new DL4JInvalidInputException(
                    "Input size (" + input.columns() + " columns; shape = " + Arrays.toString(input.shape())
                            + ") is invalid: does not match layer input size (layer # inputs = "
                            + W.size(0) + ") " + layerId());
        }

        final Set<Integer> rows = new TreeSet<>();
        // W.muli(0.5);
        INDArray secondZero = workspaceMgr.create(ArrayType.ACTIVATIONS,
                W.dataType(),
                input.size(0),
                W.size(1));
        final INDArray weightIndex = workspaceMgr.create(ArrayType.ACTIVATIONS,
                W.dataType(),
                W.size(0),
                W.size(1));

        final INDArray tensor = workspaceMgr.create(ArrayType.ACTIVATIONS,
                W.dataType(),
                W.size(0),
                W.size(1),
                W.size(1));

        final Set<Coords> coords = new HashSet<>();
        if (zeros == null) {
            zeros = workspaceMgr.create(ArrayType.ACTIVATIONS,
                    W.dataType(),
                    input.size(0),
                    input.size(1));

            final double denominator = ((double) (W.columns())) / 10;
            for (int i = 0; i < input.columns(); i++) {
                final INDArray column = input.getColumn(i);
                for (int l = 0; l < column.length(); l++) {
                    final double value = column.getDouble(l) / indWeight[i];
                    for (int j = 0; j < W.columns(); j++) {
                        if (value == j % denominator) {
                            coords.add(new Coords(l, i, i, j));
                            zeros.put(l, i, -1 * value);
                            weightIndex.put(i, j, 1);
                        }
                    }
                }
            }
        }

        INDArray mul = workspaceMgr.create(ArrayType.ACTIVATIONS, W.dataType(), input.size(0), W.size(1));
        for (Coords coord : coords) {
            final int[] inputShape = coord.getInputShape();
            final int[] weightShape = coord.getWeightShape();
            mul.put(inputShape[0], weightShape[1], -1 * input.getDouble(inputShape) / indWeight[inputShape[1]]
                    * W.getDouble(weightShape));
        }

        INDArray ret = workspaceMgr.createUninitialized(ArrayType.ACTIVATIONS, W.dataType(), input.size(0), W.size(1));
        input.castTo(ret.dataType())
             .mmuli(W, ret);//TODO Can we avoid this cast? (It sohuld be a no op if not required, however)
        ret = ret.addi(mul);

        INDArray preNorm = ret;
        if (hasLayerNorm()) {
            preNorm = (forBackprop ? ret.dup(ret.ordering()) : ret);
            Nd4j.getExecutioner().exec(new LayerNorm(preNorm, g, ret, true, 1));
        }

        if (hasBias()) {
            ret.addiRowVector(b);
        }

        if (maskArray != null) {
            applyMask(ret);
        }

        return new Pair<>(ret, preNorm);
    }

    @Override
    public boolean isPretrainLayer() {
        return false;
    }

    public double[] getIndWeight() {
        return indWeight;
    }

    public IndicatorLayerImpl setIndWeight(double[] indWeight) {
        this.indWeight = indWeight;
        return this;
    }

    public long getnOut() {
        return nOut;
    }

    public IndicatorLayerImpl setnOut(long nOut) {
        this.nOut = nOut;
        return this;
    }
}
