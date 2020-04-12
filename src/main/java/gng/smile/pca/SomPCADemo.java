package gng.smile.pca;

import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.plot.swing.Hexmap;
import smile.plot.swing.Palette;
import smile.plot.swing.PlotCanvas;
import smile.projection.PCA;
import smile.vq.Neighborhood;
import smile.vq.SOM;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SomPCADemo extends ProjectionDemo {

    JComboBox<String> corBox;

    public SomPCADemo() {
        corBox = new JComboBox<>();
        corBox.addItem("Covariance");
        corBox.addItem("Correlation");
        corBox.setSelectedIndex(0);

        optionPane.add(new JLabel("Scaling:"));
        optionPane.add(corBox);
    }

    @Override
    public JComponent learn(double[][] data, int[] labels, String[] names) {
        JPanel pane = new JPanel(new GridLayout(2, 2));

        Color[] colors = {Color.YELLOW, Color.RED, Color.BLUE};

        double[][] x = data;
        int epochs = 10;
        int nodes = 3;
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

        final double[][] codebook =
                Arrays.stream(som.neurons()).flatMap(r -> Arrays.stream(r)).toArray(double[][]::new);
        PCA pca = PCA.cor(codebook);
        pca.setProjection(2);
        final double[][] codebook2d = pca.project(codebook);

        double[][][] neurons = new double[nodes][nodes][];
        for (int i = 0, k = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++, k++) {
                neurons[i][j] = codebook2d[k];
            }
        }

        PCA dataPca = PCA.cor(x);
        dataPca.setProjection(2);
        final double[][] y = dataPca.project(x);

        final PlotCanvas plot = new PlotCanvas(MathEx.colMin(y), MathEx.colMax(y));
        plot.grid(neurons);

        for (int i = 0; i < y.length; i++) {
            plot.point(pointLegend, colors[labels[i]], y[i]);
        }

        return plot;
    }

    @Override
    public String toString() {
        return "SOM";
    }

    public static void main(String argv[]) {
        SomPCADemo demo = new SomPCADemo();
        JFrame f = new JFrame("SOM PCA");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
