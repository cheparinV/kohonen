package deep;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Students {

    private static final Logger log = LoggerFactory.getLogger(Students.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        int numLinesToSkip = 0;
        char delimiter = ',';

        RecordReader recordReader = new CSVRecordReader(numLinesToSkip, delimiter);
        final URL resource = Students.class.getClassLoader().getResource("transformed.csv");
        recordReader.initialize(new FileSplit(new File(resource.getPath())));
        int labelIndex = 8;
        int numClasses = 2;

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 10000, labelIndex, numClasses);

        NormalizerMinMaxScaler preProcessor = new NormalizerMinMaxScaler();

        log.info("Fitting with a dataset...............");
        preProcessor.fit(iterator);
        log.info("Calculated metrics");
        log.info("Min: {}", preProcessor.getMin());
        log.info("Max: {}", preProcessor.getMax());

        //iterator.setPreProcessor(preProcessor);

        final DataSet data = iterator.next();
        data.shuffle();
        data.shuffle();
        SplitTestAndTrain split = data.splitTestAndTrain(0.85);
        final DataSet trainData = split.getTrain();
        final DataSet testData = split.getTest();

        final double[] means = {0.33, 0.40, 0.32, 0.026, 0.013, 0.0027, 0.017, 0.04};
        final double[] std = {0.47, 0.28, 0.25, 0.03, 0.02, 0.015, 0.046, 0.051};
        final List<IUpdater> iUpdaters = allUpdaters();

        for (IUpdater iUpdater : iUpdaters) {
            System.out.println("Started updater " + iUpdater.getClass().getSimpleName());
            for (LossFunctions.LossFunction value : allLossFuncs()) {
                trainAndEvalModel(labelIndex, numClasses, 10000, value, iUpdater, split);
                System.out.println(value);
            }

            System.out.println("Finished updater " + iUpdater.getClass().getSimpleName());
            if (iUpdater instanceof Adam) {
                System.out.println("Learn rate: " + ((Adam) iUpdater).getLearningRate());
            }
        }

    }

    public static void trainAndEvalModel(int in, int out, int epochs,
                                         LossFunctions.LossFunction lossFunc,
                                         IUpdater iUpdater,
                                         SplitTestAndTrain split) {
        final double[] means = {0.33, 0.40, 0.32, 0.026, 0.013, 0.0027, 0.017, 0.04};
        final double[] std = {0.47, 0.28, 0.25, 0.03, 0.02, 0.015, 0.046, 0.051};

        final MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .activation(Activation.HARDSIGMOID)
                .weightInit(WeightInit.XAVIER)
                .updater(iUpdater)
                //.l1(1e-4)
                // .weightDecay(0.001)
                //.l2(1e-3)
                .list()
                //                .layer(new IndicatorLayer.Builder().nIn(labelIndex)
                //                        .nOut(4)
                //                        .indWeight(new double[]{0.05, 0.05, 0.05, 0.05, 0.05, 2, 2, 2})
                //                        .build())
                //                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                //                        .nIn(4).nOut(numClasses).build())

//                .layer(new DenseLayer.Builder().nIn(in)
//                                               .nOut(210)
//                                               //  .dropOut(0.5)
//                                               //.indWeight(std)
//                                               .build())
//                .layer(new DenseLayer.Builder().nIn(210)
//                                               .nOut(100)
//                                               // .dropOut(0.5)
//                                               .build())
//                .layer(new DenseLayer.Builder().nIn(100)
//                                               .nOut(15)
//                                               //   .dropOut(0.5)
//                                               .build())
                //.layer(new BatchNormalization())
                //                .layer(new DenseLayer.Builder().nIn(10)
                //                                               .nOut(210)
                //                                               .build())
                .layer(new DenseLayer.Builder().nIn(in)
                                               .nOut(7)
                                               //.dropOut(0.5)
                                               .build())
                .layer(new BatchNormalization())
                .layer(new DenseLayer.Builder().nIn(7)
                                               .nOut(5)
                                               //.dropOut(0.5)
                                               .build())
                .layer(new BatchNormalization())
                .layer(new OutputLayer.Builder(lossFunc)
                        .nIn(5).nOut(out).build())
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
        //     iUpdaters.add(new AdaDelta()); // 55
        //   iUpdaters.add(new AdaGrad()); // 57
        //1e-4 //61
        //        double first = 1e-4;
        //        double second = 1e-10;
        //        double third = 9e-3;
        //        double fourth = 999e-3;
        //        for (int i = 0; i < 3; i++) {
        //            iUpdaters.add(new Adam(first));
        //            first /= 2;
        //        }
        iUpdaters.add(new Adam()); // 62
        //   iUpdaters.add(new AdaMax()); //56
        //   iUpdaters.add(new AMSGrad()); //62
        iUpdaters.add(new Nadam()); //60
        //     iUpdaters.add(new Nesterovs()); //56
        //    iUpdaters.add(new NoOp()); //52
        //     iUpdaters.add(new RmsProp()); //51
        //     iUpdaters.add(new Sgd()); //49
        return iUpdaters;
    }

    public static List<LossFunctions.LossFunction> allLossFuncs() {
        final List<LossFunctions.LossFunction> list = new ArrayList<>();
        list.add(LossFunctions.LossFunction.MSE); //59
        //  list.add(LossFunctions.LossFunction.L1);//60
        //  list.add(LossFunctions.LossFunction.MCXENT);//60
        //  list.add(LossFunctions.LossFunction.KL_DIVERGENCE);//60
        //  list.add(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD);//60.5
        //  list.add(LossFunctions.LossFunction.COSINE_PROXIMITY);//60
        //  list.add(LossFunctions.LossFunction.HINGE);//60
        //  list.add(LossFunctions.LossFunction.SQUARED_HINGE);//60
        //  list.add(LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR);//60
        //  list.add(LossFunctions.LossFunction.L2);//60
        //  list.add(LossFunctions.LossFunction.MEAN_ABSOLUTE_PERCENTAGE_ERROR);
        list.add(LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR); //61
        //  list.add(LossFunctions.LossFunction.POISSON);//60
        //  list.add(LossFunctions.LossFunction.WASSERSTEIN);//39
        return list;
    }
}
