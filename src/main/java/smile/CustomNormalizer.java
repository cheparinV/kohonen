package smile;

import smile.feature.Normalizer;

import java.util.Arrays;

public class CustomNormalizer extends Normalizer {

    private double[] maxArray;

    private double[] minArray;

    private double[] diffArr;

    public void setMaxDiff(double[][] x) {
        final int size = x[0].length;
        double[] maxArray = new double[size];
        double[] minArray = new double[size];
        double[] diffArr = new double[size];
        for (int i = 0; i < size; i++) {
            int finalI = i;
            double max = Arrays.stream(x)
                               .mapToDouble(arr -> arr[finalI])
                               .max().getAsDouble();

            double min = Arrays.stream(x)
                               .mapToDouble(arr -> arr[finalI])
                               .min().getAsDouble();

            min = min > 0 ? min : 0;
            maxArray[finalI] = max;
            minArray[finalI] = min;
            diffArr[finalI] = max - min;
        }
        this.maxArray = maxArray;
        this.minArray = minArray;
        this.diffArr = diffArr;
    }

    @Override
    public double[] transform(double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = (x[i] - minArray[i]) / diffArr[i];
        }
        return y;
    }

    public double[] countIndicators() {
        double[] indicators = new double[maxArray.length];
        for (int i = 0; i < maxArray.length; i++) {
            indicators[i] = (maxArray[i] - minArray[i]) / (diffArr[i] * 20);
        }
        return indicators;
    }

    public double[] getMaxArray() {
        return maxArray;
    }

    public double[] getMinArray() {
        return minArray;
    }

    public double[] getDiffArr() {
        return diffArr;
    }
}
