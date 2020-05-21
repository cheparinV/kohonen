package gng.smile;

import gng.Data;
import smile.clustering.DBSCAN;
import smile.math.distance.EuclideanDistance;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class DBScan {

    public static void main(String[] args) throws IOException, URISyntaxException {

        final Data data = new Data();
        data.loadData();
        double[][] x = data.xData;
        final int[] y = data.yLabels;

        final DBSCAN<double[]> dbscan = DBSCAN.fit(x, new EuclideanDistance(), 3, 0.005);

        final HashMap<Integer, Map<Integer, Integer>> map = new HashMap<>();
        for (int i = 0; i < x.length; i++) {
            final int predict = dbscan.predict(x[i]);
            final Map<Integer, Integer> subMap = map.getOrDefault(predict, new HashMap<>());
            subMap.put(y[i], subMap.getOrDefault(y[i], 0) + 1);
            map.put(predict, subMap);
        }

        System.out.println(dbscan.k);


    }
}
