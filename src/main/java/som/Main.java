package som;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static Double SIGMA_CONST_INIT = 5.0;

    public static Double FIRST_TAU_CONST = 1000 / Math.log(SIGMA_CONST_INIT);

    public static Double FLUENT_CONST_INIT = 0.1;

    public static Double SECOND_TAU_CONST = 1000.0;

    public static void main(String[] args) throws IOException {
        String[] strings = new String[] {"friends", "followers"};

        final Table strongTable = readTable("/Users/cheparinv/Downloads/strong.csv", strings);
        final Table weakTable = readTable("/Users/cheparinv/Downloads/weak.csv", strings);

        System.out.println("Strong count: " + strongTable.rowCount());
        System.out.println("Weak count: " + weakTable.rowCount());
        System.out.println("All count: " + strongTable.append(weakTable).rowCount());

        Table table = strongTable;

        final NumericColumn<?>[] numericColumns = table.numberColumns();
        final ArrayList<Column<?>> doubleColumns = new ArrayList<>();
        for (NumericColumn<?> numericColumn : numericColumns) {
            final DoubleColumn doubles = numericColumn.asDoubleColumn();
            final double max = doubles.max();
            final double min = doubles.min();
            final double diff = max - min;
            doubleColumns.add(doubles.map(aDouble -> (aDouble - min) / diff));
        }
        table = Table.create(doubleColumns);
        System.out.println(table);

        final SOM som = new SOM(strings.length, 5, 5);
        som.generateNeurons();

        final List<String> columnNames = table.columnNames();
        final List<List<Double>> rows = table.stream()
                                             .map(row -> {
                                                 final ArrayList<Double> newRow = new ArrayList<>();
                                                 for (String columnName : columnNames) {
                                                     newRow.add(row.getDouble(columnName));
                                                 }
                                                 return newRow;
                                             }).collect(Collectors.toList());
        Collections.shuffle(rows);

        double sigma = sigma(0);
        double fluent = fluent(0);
        for (int epoch = 0; epoch < 2500; epoch++) {
            for (List<Double> row : rows) {
                double min = Double.MAX_VALUE;
                final List<Neuron> neurons = som.getNeurons();
                Neuron nearest = neurons.get(0);
                for (Neuron neuron : neurons) {
                    final Double euclid = euclid(row, neuron.getWeights());
                    if (euclid < min) {
                        min = euclid;
                        nearest = neuron;
                    }
                    for (Neuron localNeuron : neurons) {
                        final Double diff = neuronDiff(nearest, localNeuron, sigma, fluent);
                        if (diff != 0) {
                            localNeuron.setWeights(weights(localNeuron.getWeights(), row, diff));
                        }
                    }
                }
            }
            sigma = sigma(epoch);
            fluent = fluent(epoch);
            if (epoch % 100 == 0) {
                System.out.println("Sigma: " + sigma);
                System.out.println("Fluent: " + fluent);
                System.out.println("Epoch: " + epoch);
            }
        }
        System.out.println(som);
        final DoubleColumn xColumn = DoubleColumn.create("x", som.getNeurons().stream()
                                                           .mapToDouble(neuron -> neuron.getWeights().get(0)));

        final DoubleColumn yColumn = DoubleColumn.create("y", som.getNeurons().stream()
                                                                 .mapToDouble(neuron -> neuron.getWeights().get(1)));

        final Table result = Table.create(xColumn, yColumn);
        Plot.show(
        ScatterPlot.create("plot", result, "x", "y"));

        System.out.println();

    }

    public static List<Double> weights(List<Double> weight, List<Double> x, double diff) {
        final ArrayList<Double> newWeight = new ArrayList<>();
        for (int i = 0; i < weight.size(); i++) {
            final Double aDouble = weight.get(i);
            final Double bDouble = x.get(i);
            newWeight.add(aDouble + diff * (bDouble - aDouble));
        }
        return newWeight;
    }

    public static Double euclid(List<Double> val1, List<Double> val2) {
        Double sum = 0.0;
        for (int i = 0; i < val1.size(); i++) {
            final Double aDouble = (val1.get(i) - val2.get(i));
            sum += aDouble * aDouble;
        }
        return Math.sqrt(sum);
    }

    public static Double neuronDiff(Neuron val1, Neuron val2, Double sigma, Double fluent) {
        double xDiff = (val1.getX() - val2.getX());
        double yDiff = (val1.getY() - val2.getY());
        double diff = xDiff * xDiff + yDiff * yDiff;
        diff = Math.exp(-diff / (2 * sigma * sigma)) * fluent;
        return diff;
    }

    public static Double sigma(Integer epoch) {
        if (epoch < 1000) {
            double sigma = SIGMA_CONST_INIT * Math.exp(-epoch / FIRST_TAU_CONST);
            return sigma;
        } else {
            return SIGMA_CONST_INIT * Math.exp(-1000 / FIRST_TAU_CONST);
        }
    }

    public static Double fluent(Integer epoch) {
        double fluent = FLUENT_CONST_INIT * Math.exp(-epoch / SECOND_TAU_CONST);
        if (fluent < 0.01) {
            fluent = 0.01;
        }
        return fluent;
    }

    public static Table readTable(String path, String... columns) throws IOException {
        Table t = Table.read().usingOptions(CsvReadOptions.builder(path)
                                                          .header(true)
                                                          .separator(',')
                                                          .build());
        return Table.create(t.columns(columns));
    }
}
