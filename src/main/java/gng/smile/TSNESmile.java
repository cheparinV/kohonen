package gng.smile;

import gng.Data;
import smile.manifold.TSNE;
import smile.math.MathEx;
import smile.plot.swing.PlotCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

public class TSNESmile extends JPanel implements Runnable, ActionListener {

    private double[][] xData;

    private int[] yLabels;

    int perplexity = 20;

    JTextField perplexityField;

    private static String[] datasetName = {
            "MNIST"
    };

    double[][] data;

    int[] labels;

    JPanel optionPane;

    JComponent canvas;

    private JButton startButton;

    private JComboBox<String> datasetBox;

    char pointLegend = '@';

    public TSNESmile() {
        startButton = new JButton("Start");
        startButton.setActionCommand("startButton");
        startButton.addActionListener(this);

        datasetBox = new JComboBox<>();
        for (int i = 0; i < datasetName.length; i++) {
            datasetBox.addItem(datasetName[i]);
        }
        datasetBox.setSelectedIndex(0);
        datasetBox.setActionCommand("datasetBox");
        datasetBox.addActionListener(this);

        optionPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionPane.setBorder(BorderFactory.createRaisedBevelBorder());
        optionPane.add(startButton);
        optionPane.add(new JLabel("Dataset:"));
        optionPane.add(datasetBox);

        perplexityField = new JTextField(Integer.toString(perplexity), 5);
        optionPane.add(new JLabel("Perplexity:"));
        optionPane.add(perplexityField);

        setLayout(new BorderLayout());
        add(optionPane, BorderLayout.NORTH);
    }

    public JComponent learn() {
        JPanel pane = new JPanel(new GridLayout(1, 2));

        //        PCA pca = PCA.fit(data);
        //        pca.setProjection(50);
        //        double[][] X = pca.project(data);
        double[][] X = data;
        long clock = System.currentTimeMillis();
        TSNE tsne = new TSNE(X, 2, perplexity, 200, 1000);
        System.out.format("Learn t-SNE from %d samples in %dms\n", data.length, System.currentTimeMillis() - clock);

        double[][] y = tsne.coordinates;

        PlotCanvas plot = new PlotCanvas(MathEx.colMin(y), MathEx.colMax(y));

        Color[] colors = {Color.YELLOW, Color.RED, Color.BLUE};

        for (int i = 0; i < y.length; i++) {
            plot.point(pointLegend, colors[labels[i]], y[i]);
        }

        plot.setTitle("t-SNE");
        pane.add(plot);

        return pane;
    }

    @Override
    public void run() {
        startButton.setEnabled(false);
        datasetBox.setEnabled(false);
        perplexityField.setEnabled(false);

        try {
            JComponent plot = learn();
            if (plot != null) {
                if (canvas != null) {
                    remove(canvas);
                }
                canvas = plot;
                add(plot, BorderLayout.CENTER);
            }
            validate();
        } catch (Exception ex) {
            System.err.println(ex);
        }

        startButton.setEnabled(true);
        datasetBox.setEnabled(true);
        perplexityField.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("startButton".equals(e.getActionCommand())) {

            try {
                loadData();
                data = xData;
                labels = yLabels;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failed to load dataset.", "ERROR", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

            try {
                perplexity = Integer.parseInt(perplexityField.getText().trim());
                if (perplexity < 10 || perplexity > 300) {
                    JOptionPane.showMessageDialog(this, "Invalid Perplexity: " + perplexity, "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid K: " + perplexityField.getText(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Thread thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public String toString() {
        return "t-SNE";
    }

    public static void main(String argv[]) {
        TSNESmile demo = new TSNESmile();
        JFrame f = new JFrame("t-SNE");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }

    public void loadData() throws IOException, URISyntaxException {
        final Data data = new Data();
        data.loadData();
        xData = data.xData;
        yLabels = data.yLabels;
    }
}
