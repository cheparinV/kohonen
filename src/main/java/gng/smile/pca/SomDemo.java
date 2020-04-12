package gng.smile.pca;

import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.mds.SammonMapping;
import smile.plot.swing.Hexmap;
import smile.plot.swing.Palette;
import smile.plot.swing.PlotCanvas;
import smile.vq.Neighborhood;
import smile.vq.SOM;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SomDemo extends ProjectionDemo {

    JComboBox<String> corBox;

    public SomDemo() {
        corBox = new JComboBox<>();
        corBox.addItem("Covariance");
        corBox.addItem("Correlation");
        corBox.setSelectedIndex(0);

        optionPane.add(new JLabel("Scaling:"));
        optionPane.add(corBox);
    }

    @Override
    public JComponent learn(double[][] data, int[] labels, String[] names) {
        JPanel pane = new JPanel(new GridLayout(1, 2));

        Color[] colors = {Color.YELLOW, Color.RED, Color.BLUE};

        double[][] x = data;
        int epochs = 10;
        int nodes = 40;
        double[][][] lattice = SOM.lattice(nodes, nodes, x);
        SOM som = new SOM(lattice,
                TimeFunction.constant(0.1),
                Neighborhood.Gaussian(1, x.length * epochs / 4));

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

        double[][] umatrix = som.umatrix();
        PlotCanvas umatrixPlot = Hexmap.plot(umatrix, Palette.jet(256));
        umatrixPlot.setTitle("U-Matrix");
        //pane.add(umatrixPlot);

//        final double[] dist = Arrays.stream(som.umatrix()).flatMapToDouble(r -> Arrays.stream(r)).toArray();
//        final PlotCanvas plotHist = Histogram.plot(dist, 100);
//        final GaussianMixture mixture = GaussianMixture.fit(dist);
//
//        final double minDist = MathEx.min(dist);
//        double w = (MathEx.max(dist) - minDist) / 50;
//        double[][] pdf = new double[25][2];
//        for (int i = 0; i < pdf.length; i++) {
//            pdf[i][0] = minDist + i * w;
//            pdf[i][1] = mixture.p(pdf[i][0]) * w;
//        }
//
//        plotHist.line(pdf, Color.RED);
//        pane.add(plotHist);

        final double[][] codebook =
                Arrays.stream(som.neurons()).flatMap(r -> Arrays.stream(r)).toArray(double[][]::new);
        final double[][] codebook2d = SammonMapping.of(MathEx.pdist(codebook), 2).coordinates;

        double[][][] neurons = new double[nodes][nodes][];
        for (int i = 0, k = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++, k++) {
                neurons[i][j] = codebook2d[k];
            }
        }
        final double[][] y = SammonMapping.of(MathEx.pdist(x), 2).coordinates;
        //double[][] y = data;

        final PlotCanvas plot = new PlotCanvas(MathEx.colMin(y), MathEx.colMax(y));
        plot.grid(neurons);
        //plot.grid(som.neurons());

        for (int i = 0; i < y.length; i++) {
            plot.point(pointLegend, colors[labels[i]], y[i]);
        }
        pane.add(plot);

        return pane;
    }

    @Override
    public String toString() {
        return "SOM";
    }

    public static void main(String argv[]) {
        SomDemo demo = new SomDemo();
        JFrame f = new JFrame("SOM Sammon");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
