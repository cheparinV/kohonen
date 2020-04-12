package gng.smile.pca;

import smile.math.MathEx;
import smile.mds.SammonMapping;
import smile.plot.swing.PlotCanvas;
import smile.vq.GrowingNeuralGas;
import smile.vq.hebb.Neuron;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GngDemo extends ProjectionDemo {

    JComboBox<String> corBox;

    public GngDemo() {
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
        int epochs = 20;
        int nodes = 3;

        //final double[][] y = SammonMapping.of(MathEx.pdist(x), 2).coordinates;
        double[][] y = data;
        final PlotCanvas plot = new PlotCanvas(MathEx.colMin(y), MathEx.colMax(y));
        for (int i = 0; i < y.length; i++) {
            plot.point(pointLegend, colors[labels[i]], y[i]);
        }
        GrowingNeuralGas gng = new GrowingNeuralGas(x[0].length);

        for (int e = 0; e < epochs; e++) {
            for (int i : MathEx.permutate(x.length)) {
                gng.update(x[i]);
            }
        }

        final Neuron[] neurons = gng.neurons();
        final double[][] codebook = Arrays.stream(neurons).map(n -> n.w).toArray(double[][]::new);
        final double[][] codebook2d = SammonMapping.of(MathEx.pdist(codebook), 2).coordinates;
        //plot.points(codebook2d, pointLegend);

        plot.points(Arrays.stream(neurons).map(n -> n.w).toArray(double[][]::new), '@', Color.GREEN);
        //neurons[0]
        Arrays.stream(neurons).forEach(neuron -> {
            neuron.edges.stream().forEach(edge -> {
                double[][] e = {neuron.w, edge.neighbor.w};
                plot.line(e, Color.BLUE);
            });
        });

        return plot;
    }

    @Override
    public String toString() {
        return "SOM";
    }

    public static void main(String argv[]) {
        GngDemo demo = new GngDemo();
        JFrame f = new JFrame("GNG Sammon");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
