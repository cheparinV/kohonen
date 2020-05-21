package smile;

import org.apache.commons.csv.CSVFormat;
import smile.base.mlp.Layer;
import smile.base.mlp.OutputFunction;
import smile.classification.MLP;
import smile.data.DataFrame;
import smile.data.vector.IntVector;
import smile.io.Read;
import smile.math.MathEx;
import smile.validation.Error;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class SmileMLP {

    public static void main(String[] args) throws IOException, URISyntaxException {

        String[] strings = new String[] {"friends", "followers", "photos", "pages", "videos"};
        CSVFormat format = CSVFormat.newFormat('\t').withFirstRecordAsHeader().withIgnoreEmptyLines()
                                    .withTrim();
        DataFrame csv = Read.csv("/Users/cheparinv/Downloads/strong.tsv", format);
        DataFrame strong = filter(csv.select(strings));
        DataFrame weak = filter(Read.csv("/Users/cheparinv/Downloads/weak.tsv", format).select(strings));
        DataFrame middle = Read.csv("/Users/cheparinv/Downloads/middle.tsv", format).select(strings);
        int[] videos = middle.vector("videos").toIntArray();
        middle.drop("videos");
        middle = middle.select("friends", "followers", "photos", "pages").merge(IntVector.of("videos", videos));
        middle = filter(middle);
        //        strong = strong.slice(0, 970);
        //        weak = weak.slice(0, 970);
        //        middle = middle.slice(0, 970);
        CustomNormalizer normalizer = new CustomNormalizer();
        DataFrame union = strong.union(weak);//.union(middle);
        double[][] data = union.toArray();
        normalizer.setMaxDiff(data);
        final double[] indicators = maxIndicators(data);//normalizer.countIndicators();
        final double[][] x = data;// normalizer.transform(data);
        final int[] y = new int[x.length];
        Arrays.fill(y, 0, strong.size() - 1, 1);
        Arrays.fill(y, strong.size(), weak.size() + strong.size() - 1, 0);
        // Arrays.fill(y, weak.size()+strong.size(), y.length, 2);

        int p = x[0].length;
        int k = MathEx.max(y) + 1;

        final long start = System.currentTimeMillis();

        //        final MLP model = new MLP(p,
        //                Layer.sigmoid(210),
        //                Layer.sigmoid(10),
        //                Layer.mse(k, OutputFunction.SIGMOID)
        //        );

        //MathEx.setSeed(19650218); // to get repeatable results.
        int[] prediction = CustomCrossValidation.classification(10, x, y, (xi, yi) -> {
            final MLP model = new MLP(p,
                    Layer.sigmoid(210),//, indicators),
                    Layer.sigmoid(10),
                    Layer.mse(k, OutputFunction.SIGMOID)
            );
            for (int epoch = 1; epoch <= 10; epoch++) {
                int[] permutation = MathEx.permutate(xi.length);
                for (int i : permutation) {
                    model.update(xi[i], yi[i]);
                }
            }
            return model;
        });

        final long end = System.currentTimeMillis() - start;

        double error = Error.of(y, prediction);
        final int i = y[y.length - 1];
        System.out.println("Error = " + error / y.length);

        System.out.println("Seconds " + end / 1000);
    }

    private static DataFrame filter(DataFrame dataFrame) {
        return DataFrame.of(dataFrame.stream()
                                     .filter(row -> row.getInt("friends") <= 900
                                             && row.getInt("followers") <= 600
                                             && row.getInt("photos") <= 600
                                             && row.getInt("videos") <= 600
                                             && row.getInt("pages") <= 600));
    }

    private static double[] maxIndicators(double[][] x) {
        final int size = x[0].length;
        double[] indicators = new double[size];
        for (int i = 0; i < size; i++) {
            int finalI = i;
            final double max = Arrays.stream(x)
                                     .mapToDouble(arr -> arr[finalI])
                                     .max().getAsDouble();
            indicators[i] = max/20;
        }
        return indicators;
    }
}
