package gng.smile.pca;

import gng.Data;
import org.apache.commons.csv.CSVFormat;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.io.Read;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ProjectionDemo extends JPanel implements Runnable, ActionListener {

    private Data dataObj = new Data();

    private static String[] datasetName = {
            "IRIS", "US Arrests", "Food Nutrition",
            "Pen Digits", "COMBO-17"
    };

    private static String[] datasource = {
            "classification/iris.txt",
            "projection/USArrests.txt",
            "projection/food.txt",
            "classification/pendigits.txt",
            "projection/COMBO17.dat"
    };

    protected static DataFrame[] dataset = new DataFrame[datasetName.length];

    protected static Formula[] formula = {
            Formula.lhs("Species"),
            Formula.rhs("Murder", "Assault", "UrbanPop", "Rape"),
            null,
            Formula.lhs("V17"),
            null,
    };

    protected static int datasetIndex = 10;

    JPanel optionPane;

    JComponent canvas;

    private JButton startButton;

    private JComboBox<String> datasetBox;

    protected char pointLegend = '.';

    /**
     * Constructor.
     */
    public ProjectionDemo() {
        loadData(datasetIndex);

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

        setLayout(new BorderLayout());
        add(optionPane, BorderLayout.NORTH);
    }

    /**
     * Execute the projection algorithm and return a swing JComponent representing
     * the clusters.
     */
    public abstract JComponent learn(double[][] data, int[] labels, String[] names);

    @Override
    public void run() {
        startButton.setEnabled(false);
        datasetBox.setEnabled(false);

        try {
            dataObj.loadData();
            double[][] data = dataObj.xData;
            int[] labels = dataObj.yLabels;
            String[] names = null;

            JComponent plot = learn(data, labels, names);
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
            ex.printStackTrace();
        }

        startButton.setEnabled(true);
        datasetBox.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("startButton".equals(e.getActionCommand())) {
            datasetIndex = datasetBox.getSelectedIndex();
            loadData(datasetIndex);
            pointLegend = '.';
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    private void loadData(int datasetIndex) {
        if (datasetIndex == 10) {
            return;
        }
        if (dataset[datasetIndex] != null) {
            return;
        }

        CSVFormat format = CSVFormat.DEFAULT.withDelimiter('\t');
        if (datasetIndex != 3) {
            format = format.withFirstRecordAsHeader();
        }

        try {
            dataset[datasetIndex] = Read.csv(smile.util.Paths.getTestData(datasource[datasetIndex]), format);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, String.format("Failed to load dataset %s", datasetName[datasetIndex]),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println(ex);
        }
    }
}
