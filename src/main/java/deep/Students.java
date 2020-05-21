package deep;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.condition.column.LongColumnCondition;
import org.datavec.api.transform.filter.ConditionFilter;
import org.datavec.api.transform.schema.Schema;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Students {

    private static final Logger log = LoggerFactory.getLogger(Students.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        int numLinesToSkip = 1;
        char delimiter = ',';

        Schema inputDataSchema = new Schema.Builder()
                .addColumnsLong("friends", "followers", "photos", "videos", "pages", "type")
                .build();

        TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
                .filter(new ConditionFilter(new LongColumnCondition("friends", ConditionOp.GreaterThan, 1000)))
                .build();

        RecordReader recordReader = new CSVRecordReader(numLinesToSkip, delimiter);
        recordReader.initialize(new FileSplit(new File("/Users/cheparinv/Documents/dl4j", "topBotLims.csv")));

        int labelIndex = 8;
        int numClasses = 2;

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 10000, labelIndex, numClasses);
        NormalizerMinMaxScaler preProcessor = new NormalizerMinMaxScaler();

        log.info("Fitting with a dataset...............");
        preProcessor.fit(iterator);
        log.info("Calculated metrics");
        log.info("Min: {}", preProcessor.getMin());
        log.info("Max: {}", preProcessor.getMax());

        iterator.setPreProcessor(preProcessor);

        final DataSet data = iterator.next();
        data.shuffle();
        data.shuffle();
        SplitTestAndTrain split = data.splitTestAndTrain(0.75);
        final DataSet trainData = split.getTrain();
        final DataSet testData = split.getTest();

        final double[] means = {0.33, 0.40, 0.32, 0.026, 0.013, 0.0027, 0.017, 0.04};
        final double[] std = {0.47, 0.28, 0.25, 0.03, 0.02, 0.015, 0.046, 0.051};
        final List<IUpdater> iUpdaters = allUpdaters();

        for (IUpdater iUpdater : iUpdaters) {
            System.out.println("Started updater " + iUpdater.getClass().getSimpleName());
            trainAndEvalModel(labelIndex, numClasses, 10000, iUpdater, split);
            System.out.println("Finished updater " + iUpdater.getClass().getSimpleName());
        }

    }

    public static void trainAndEvalModel(int in, int out, int epochs,

                                         IUpdater iUpdater,
                                         SplitTestAndTrain split) {
        final MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.XAVIER)
                .updater(iUpdater)
                //.l1(1e-4)
                .weightDecay(0.001)
                //.l2(1e-3)
                .list()
                //                .layer(new IndicatorLayer.Builder().nIn(labelIndex)
                //                        .nOut(4)
                //                        .indWeight(new double[]{0.05, 0.05, 0.05, 0.05, 0.05, 2, 2, 2})
                //                        .build())
                //                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                //                        .nIn(4).nOut(numClasses).build())

                //                .layer(new IndicatorLayer.Builder().nIn(labelIndex)
                //                        .nOut(210)
                //                        .dropOut(0.4)
                //                        .indWeight(std)
                //                        .build())
                //                .layer(new DenseLayer.Builder().nIn(in)
                //                                               .nOut(10)
                //                                               .build())
                //.layer(new BatchNormalization())
                //                .layer(new DenseLayer.Builder().nIn(10)
                //                                               .nOut(210)
                //                                               .build())
                .layer(new DenseLayer.Builder().nIn(in)
                                               .nOut(5)
                                               .build())
                //   .layer(new BatchNormalization())
                .layer(new DenseLayer.Builder().nIn(5)
                                               .nOut(3)
                                               .build())
                //.layer(new BatchNormalization())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(3).nOut(out).build())
                .build();

        final MultiLayerNetwork model = new MultiLayerNetwork(conf);

        model.init();
        model.setListeners(new ScoreIterationListener(500));
        for (int i = 0; i < epochs; i++) {
            model.fit(split.getTrain());
        }
        //evaluate the model on the test set
        Evaluation eval = new Evaluation(out);
        INDArray output = model.output(split.getTest().getFeatures());
        eval.eval(split.getTest().getLabels(), output);
        log.info(eval.stats());
    }

    public static List<IUpdater> allUpdaters() {
        final ArrayList<IUpdater> iUpdaters = new ArrayList<IUpdater>();
        // iUpdaters.add(new AdaDelta()); // 55
        // iUpdaters.add(new AdaGrad()); // 57
        iUpdaters.add(new Adam()); // 62
        // iUpdaters.add(new AdaMax()); //56
        // iUpdaters.add(new AMSGrad()); //62
        iUpdaters.add(new Nadam()); //60
        // iUpdaters.add(new Nesterovs()); //56
        // iUpdaters.add(new NoOp()); //52
        // iUpdaters.add(new RmsProp()); //51
        // iUpdaters.add(new Sgd()); //49
        return iUpdaters;
    }
}
