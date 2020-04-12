package gng.smile;

import gng.Data;
import smile.clustering.CentroidClustering;
import smile.clustering.GMeans;
import smile.clustering.KMeans;
import smile.clustering.XMeans;
import smile.math.MathEx;
import smile.vq.GrowingNeuralGas;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GasNeural {

    private double[][] xData;

    private int[] yLabels;

    public static void main(String[] args) throws IOException, URISyntaxException {

        final GasNeural gasNeural = new GasNeural();

        gasNeural.loadData();
        double[][] x = gasNeural.xData;
        final int[] y = gasNeural.yLabels;

        GrowingNeuralGas model = new GrowingNeuralGas(x[0].length);
        for (int i = 1; i <= 22; i++) {
            for (int j : MathEx.permutate(x.length)) {
                model.update(x[j]);
            }
            System.out.format("%d neurons after %d epochs%n", model.neurons().length, i);
        }

        double error = 0.0;
        for (double[] xi : x) {
            double[] yi = model.quantize(xi);
            error += MathEx.distance(xi, yi);
        }
        error /= x.length;
        System.out.format("Training Quantization Error = %.4f%n", error);

        final HashMap<double[], Map<Integer, Integer>> map = new HashMap<>();
        for (int i = 0; i < x.length; i++) {
            final double[] quantize = model.quantize(x[i]);
            final Map<Integer, Integer> subMap = map.getOrDefault(quantize, new HashMap<>());
            subMap.put(y[i], subMap.getOrDefault(y[i], 0) + 1);
            map.put(quantize, subMap);
        }

        final HashMap<double[], Integer> byClass = new HashMap<>();
        map.entrySet().forEach(mapEntry -> {
            final Integer integer = mapEntry.getValue().entrySet().stream()
                                            .sorted(Comparator.comparingInt(Map.Entry::getValue))
                                            .map(Map.Entry::getKey)
                                            .findFirst().get();
            byClass.put(mapEntry.getKey(), integer);
            System.out.println(Arrays.toString(mapEntry.getKey()) + ": " + integer);
        });

        int finalError = 0;
        for (int i = 0; i < x.length; i++) {
            final double[] quantize = model.quantize(x[i]);
            finalError += byClass.get(quantize) != y[i] ? 1 : 0;
        }

        System.out.println(((double) finalError / x.length));

        final double[][] weights = Arrays.stream(model.neurons()).map(n -> n.w).toArray(double[][]::new);
        final XMeans xMeans = XMeans.fit(weights, 5);
        System.out.println("Xmeans : " + xMeans);
        analyze(xMeans, x, y);

        final KMeans kMeans = KMeans.fit(weights, 5);
        System.out.println("KMeans : " + kMeans);
        analyze(kMeans, x, y);

        final GMeans gMeans = GMeans.fit(weights, 5);
        System.out.println("Gmeans : " + gMeans);
        analyze(gMeans, x, y);
    }

    public static void analyze(CentroidClustering model, double[][] x, int[] y) {
        final HashMap<Integer, List<Integer>> centroidByClass = new HashMap<>();
        for (int i = 0; i < x.length; i++) {
            final int predict = model.predict(x[i]);
            final List<Integer> orDefault = centroidByClass.getOrDefault(predict, new ArrayList<>());
            orDefault.add(y[i]);
            centroidByClass.put(predict, orDefault);
        }

        final long[] error = {0};
        centroidByClass.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " centroid : ");
            final Map<Integer, Long> collect = entry.getValue().stream()
                                                    .collect(Collectors
                                                            .groupingBy(Function.identity(), Collectors.counting()));
            collect.entrySet().forEach(longEntry -> {
                System.out.println(longEntry.getKey() + " " + longEntry.getValue());
            });
            error[0] += collect.values().stream().max(Long::compareTo).get();
        });

        System.out.println("Accuracy : " + ((double) error[0]) / x.length);
    }

    public void loadData() throws IOException, URISyntaxException {
        final Data data = new Data();
        data.loadData();
        xData = data.xData;
        yLabels = data.yLabels;
    }
}
