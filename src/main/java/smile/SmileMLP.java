package smile;

import org.apache.commons.csv.CSVFormat;
import smile.base.mlp.Layer;
import smile.base.mlp.OutputFunction;
import smile.classification.MLP;
import smile.data.DataFrame;
import smile.data.vector.IntVector;
import smile.io.Read;
import smile.math.MathEx;
import smile.validation.CrossValidation;
import smile.validation.Error;
import som.DataProcessing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class SmileMLP {

    public static void main(String[] args) throws IOException, URISyntaxException {

        final DataProcessing processing = new DataProcessing();
        String[] strings = new String[] {"friends", "followers", "photos", "pages", "videos"};
        //String[] strings = new String[] {"friends", "followers"};

//        final Table strongTable = processing.addClassColumnToTable(
//                processing.readTable("/Users/cheparinv/Downloads/strong.csv", strings), 1.0);
//
//        final Table weakTable = processing.addClassColumnToTable(
//                processing.readTable("/Users/cheparinv/Downloads/weak.csv", strings), 0.0);
//
//        System.out.println("Strong count: " + strongTable.rowCount());
//        System.out.println("Weak count: " + weakTable.rowCount());
//        System.out.println("All count: " + strongTable.append(weakTable).rowCount());
//
//        Table rowTable = processing.normalizedTable(strongTable);
//        Table table = rowTable.copy();
//        table.removeColumns("class");
//        Plot.show(ScatterPlot.create("data", table, "friends", "followers"));
//
//        final int weightSize = strings.length;
//
//        final List<List<Double>> rows = processing.tableToListOfVectors(table);
//        Collections.shuffle(rows);

        final CSVFormat format = CSVFormat.newFormat('\t').withFirstRecordAsHeader().withIgnoreEmptyLines()
                .withTrim();
        final DataFrame csv = Read.csv("/Users/cheparinv/Downloads/strong.tsv", format);
        final DataFrame strong = csv.select(strings).slice(0, 1100);
        final DataFrame weak = Read.csv("/Users/cheparinv/Downloads/weak.tsv", format).select(strings).slice(0, 1000);
        final DataFrame middle = Read.csv("/Users/cheparinv/Downloads/middle.tsv", format).select(strings).slice(0, 1000);
        final int[] videos = middle.vector("videos").toIntArray();
        middle.drop("videos");
        final DataFrame merge =
                middle.select("friends", "followers", "photos", "pages").merge(IntVector.of("videos", videos));
        //middle.structure().column("videos").field().
        //final Normalizer normalizer = new Normalizer(Normalizer.Norm.Inf);
        final CustomNormalizer normalizer = new CustomNormalizer();
        final DataFrame union = strong.union(weak).union(merge);
        final double[][] data = union.toArray();
        normalizer.setMaxDiff(data);
        final double[][] x = normalizer.transform(data);
        final int[] y = new int[x.length];
        Arrays.fill(y, 0, strong.size()-1, 1);
        Arrays.fill(y, strong.size(), weak.size()+strong.size()-1, 0);
        Arrays.fill(y, weak.size()+strong.size(), y.length, 2);

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
                    Layer.sigmoid(210),
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

        int error = Error.of(y, prediction);
        final int i = y[y.length - 1];
        System.out.println("Error = " + error);
        System.out.println("Seconds "+ end/1000);
    }
}
