package gng.smile;

import gng.Data;
import smile.clustering.GMeans;
import smile.clustering.KMeans;
import smile.clustering.XMeans;
import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.vq.Neighborhood;
import smile.vq.SOM;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SOMNeural {

    public static void main(String[] args) throws IOException, URISyntaxException {

        final Data data = new Data();
        data.loadData();
        double[][] x = data.xData;
        int[] y = data.yLabels;

        int epochs = 10;
        int nodes = 20;
        double[][][] lattice = SOM.lattice(nodes, nodes, x);
        SOM som = new SOM(lattice, TimeFunction.constant(0.1), Neighborhood.Gaussian(1, x.length * epochs / 4));

        for (int i = 1; i <= epochs; i++) {
            for (int j : MathEx.permutate(x.length)) {
                som.update(x[j]);
            }

            double somError = 0.0;
            for (double[] xi : x) {
                double[] yi = som.quantize(xi);
                somError += MathEx.distance(xi, yi);
            }
            somError /= x.length;
            System.out.format("Training Quantization Error = %.4f after %d epochs%n", somError, i);
        }

        final HashMap<double[], Map<Integer, Integer>> map = new HashMap<>();
        for (int i = 0; i < x.length; i++) {
            final double[] quantize = som.quantize(x[i]);
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

        int error = 0;
        for (int i = 0; i < x.length; i++) {
            final double[] quantize = som.quantize(x[i]);
            error += byClass.get(quantize) != y[i] ? 1 : 0;
        }
        System.out.println(((double) error / x.length));

        final double[][] weigths =
                Arrays.stream(som.neurons()).flatMap(r -> Arrays.stream(r)).toArray(double[][]::new);

        final XMeans xMeans = XMeans.fit(weigths, 10);
        System.out.println(xMeans);
        GasNeural.analyze(xMeans, x, y);

        final KMeans kMeans = KMeans.fit(weigths, 10);
        System.out.println(kMeans);
        GasNeural.analyze(kMeans, x, y);

        final GMeans gMeans = GMeans.fit(weigths, 10);
        System.out.println(gMeans);
        GasNeural.analyze(gMeans, x, y);

    }

}
