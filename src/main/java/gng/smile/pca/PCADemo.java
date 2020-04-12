package gng.smile.pca;

import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.plot.swing.Hexmap;
import smile.plot.swing.Palette;
import smile.plot.swing.PlotCanvas;
import smile.projection.PCA;
import smile.vq.GrowingNeuralGas;
import smile.vq.Neighborhood;
import smile.vq.SOM;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PCADemo extends ProjectionDemo {

    JComboBox<String> corBox;

    public PCADemo() {
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

        long clock = System.currentTimeMillis();
        PCA pca = PCA.cor(data);
        System.out.format("Learn PCA from %d samples in %dms\n", data.length, System.currentTimeMillis() - clock);

        Color[] colors = {Color.YELLOW, Color.RED, Color.BLUE};
        pca.setProjection(2);
        double[][] y = pca.project(data);

        PlotCanvas plot = new PlotCanvas(MathEx.colMin(y), MathEx.colMax(y));
        if (names != null) {
            plot.points(y, names);
        } else if (labels != null) {
            for (int i = 0; i < y.length; i++) {
                plot.point(pointLegend, colors[labels[i]], y[i]);
            }
        } else {
            plot.points(y, pointLegend);
        }

        plot.setTitle("PCA");
        pane.add(plot);

        GrowingNeuralGas gng = new GrowingNeuralGas(data[0].length);
        for (int i = 1; i <= 10; i++) {
            for (int j : MathEx.permutate(data.length)) {
                gng.update(data[j]);
            }
            System.out.format("%d neurons after %d epochs%n", gng.neurons().length, i);
        }

        //ScatterPlot.plot()

        double error = 0.0;
        for (double[] xi : data) {
            double[] yi = gng.quantize(xi);
            error += MathEx.distance(xi, yi);
        }
        error /= data.length;
        System.out.format("Training Quantization Error = %.4f%n", error);

        final double[][] doubles = Arrays.stream(gng.neurons()).map(neur -> neur.w)
                                         .toArray(double[][]::new);

        PCA pcaGng = PCA.cor(doubles);
        System.out.format("Learn PCA from %d samples in %dms\n", doubles.length, System.currentTimeMillis() - clock);

        pcaGng.setProjection(2);
        double[][] yGng = pcaGng.project(doubles);

        PlotCanvas plotGng = new PlotCanvas(MathEx.colMin(yGng), MathEx.colMax(yGng));
        plotGng.points(yGng, pointLegend);

        plotGng.setTitle("PCA/GNG");
        pane.add(plotGng);


        double[][] x = data;
        int epochs = 10;
        double[][][] lattice = SOM.lattice(5, 5, x);
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
        pane.add(umatrixPlot);

        PlotCanvas plotSom = new PlotCanvas(MathEx.colMin(yGng), MathEx.colMax(yGng));
        plotSom.grid(som.neurons());
        plotSom.setTitle("som");
        pane.add(plotSom);

        return pane;
    }

    @Override
    public String toString() {
        return "Principal Component Analysis";
    }

    public static void main(String argv[]) {
        PCADemo demo = new PCADemo();
        JFrame f = new JFrame("Principal Component Analysis");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
