package gng;

import org.apache.commons.csv.CSVFormat;
import smile.CustomNormalizer;
import smile.data.DataFrame;
import smile.data.vector.IntVector;
import smile.io.Read;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Data {

    public double[][] xData;

    public int[] yLabels;

    public void loadData() throws IOException, URISyntaxException {
        String[] strings = new String[] {"friends", "followers", "photos", "pages", "videos"};
        // String[] strings = new String[] {"friends", "followers"};
        final CSVFormat format = CSVFormat.newFormat('\t').withFirstRecordAsHeader().withIgnoreEmptyLines()
                                          .withTrim();
        final DataFrame csv = Read.csv("/Users/cheparinv/Downloads/strong.tsv", format);
        DataFrame strong = csv.select(strings);
        strong = filter(strong).slice(0, 970);
        DataFrame weak = Read.csv("/Users/cheparinv/Downloads/weak.tsv", format).select(strings);
        weak = filter(weak);
        DataFrame middle = Read.csv("/Users/cheparinv/Downloads/middle.tsv", format).select(strings);
        final int[] videos = middle.vector("videos").toIntArray();
        middle.drop("videos");
        middle = middle.select("friends", "followers", "photos", "pages").merge(IntVector.of("videos", videos));
        middle = filter(middle).slice(0, 970);
        final CustomNormalizer normalizer = new CustomNormalizer();
        DataFrame union = strong.union(weak).union(middle);
        union = filter(union);

        final double[][] data = union.toArray();
        normalizer.setMaxDiff(data);
        final double[][] x = normalizer.transform(data);
        final int[] y = new int[x.length];
        Arrays.fill(y, 0, strong.size() - 1, 1);
        Arrays.fill(y, strong.size(), weak.size() + strong.size() - 1, 0);
        Arrays.fill(y, weak.size() + strong.size(), y.length, 2);

        xData = x;
        yLabels = y;
    }

    private DataFrame filter(DataFrame dataFrame) {
        return DataFrame.of(dataFrame.stream()
                                     .filter(row -> row.getInt("friends") <= 600
                                             && row.getInt("followers") <= 600
                                             && row.getInt("photos") <= 600
                                             && row.getInt("videos") <= 600
                                             && row.getInt("pages") <= 600));
    }
}
