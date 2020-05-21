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
import java.util.TreeMap;
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

        final Map<Integer, Double> map = new TreeMap<>();
        for (int i = 1; i <= 40; i++) {
            gas(i, x, y, map);
        }

        map.forEach((key, value) -> System.out.println(key + "," + value.floatValue()));
    }

    public static void gas(int epochs, double[][] x, int[] y, Map<Integer, Double> outMap) {
        final GasNeural gasNeural = new GasNeural();

        GrowingNeuralGas model = new GrowingNeuralGas(x[0].length);
        for (int i = 1; i <= epochs; i++) {
            for (int j : MathEx.permutate(x.length)) {
                model.update(x[j]);
            }
            //System.out.format("%d neurons after %d epochs%n", model.neurons().length, i);
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
            //System.out.println(Arrays.toString(mapEntry.getKey()) + ": " + integer);
        });

        int finalError = 0;
        for (int i = 0; i < x.length; i++) {
            final double[] quantize = model.quantize(x[i]);
            finalError += byClass.get(quantize) != y[i] ? 1 : 0;
        }
        System.out.println("Neurons : " + model.neurons().length);
        final double accuracy = 1.0 - ((double) finalError / x.length);
        System.out.println(accuracy);

        final double[][] weights = Arrays.stream(model.neurons()).map(n -> n.w).toArray(double[][]::new);
        final XMeans xMeans = XMeans.fit(weights, 20);
        System.out.println("Xmeans : " + xMeans);
        final double first = analyze(xMeans, x, y);

        final KMeans kMeans = KMeans.fit(weights, 20);
        System.out.println("KMeans : " + kMeans);
        final double second = analyze(kMeans, x, y);

        final GMeans gMeans = GMeans.fit(weights, 20);
        System.out.println("Gmeans : " + gMeans);
        final double third = analyze(gMeans, x, y);

        double max = Arrays.stream(new double[]{first, second, third}).max().getAsDouble();

        outMap.put(model.neurons().length, max);
    }

    public static double analyze(CentroidClustering model, double[][] x, int[] y) {
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

        final double accuracy = ((double) error[0]) / x.length;
        System.out.println("Accuracy : " + accuracy);
        return accuracy;
    }

    public void loadData() throws IOException, URISyntaxException {
        final Data data = new Data();
        data.loadData();
        xData = data.xData;
        yLabels = data.yLabels;
    }
}
