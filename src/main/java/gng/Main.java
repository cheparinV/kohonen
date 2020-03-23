package gng;

import com.google.common.collect.Lists;
import org.apache.commons.math3.random.RandomDataGenerator;
import som.DataProcessing;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        final DataProcessing processing = new DataProcessing();
        //String[] strings = new String[] {"friends", "followers", "photos", "pages", "videos"};
        String[] strings = new String[] {"friends", "followers"};

        final Table strongTable = processing.addClassColumnToTable(
                processing.readTable("/Users/cheparinv/Downloads/strong.csv", strings), 1.0);

        final Table weakTable = processing.addClassColumnToTable(
                processing.readTable("/Users/cheparinv/Downloads/weak.csv", strings), 0.0);

        System.out.println("Strong count: " + strongTable.rowCount());
        System.out.println("Weak count: " + weakTable.rowCount());
        System.out.println("All count: " + strongTable.append(weakTable).rowCount());

        Table rowTable = processing.normalizedTable(strongTable);
        Table table = rowTable.copy();
        table.removeColumns("class");
        Plot.show(ScatterPlot.create("data", table, "friends", "followers"));

        final int weightSize = strings.length;

        final List<List<Double>> rows = processing.tableToListOfVectors(table);
        Collections.shuffle(rows);

        Double nuWinner = 0.05;
        Double nuNeighbour = 0.0006;
        int maxAge = 5;
        int lambda = 200;
        int maxNeuronsCount = 10;
        double alpha = 0.6;

        final ArrayList<GasNeuron> neurons = new ArrayList<>();
        neurons.add(createNeuron(weightSize));
        neurons.add(createNeuron(weightSize));
        final LinkMap linkMap = new LinkMap();
        linkMap.addToKey(neurons.get(0), neurons.get(1));

        int iteration = 1;
        while (neurons.size() < maxNeuronsCount) {
            for (List<Double> row : rows) {
                double min = Double.MAX_VALUE;
                double minSecond = Double.MAX_VALUE;
                GasNeuron first = neurons.get(0);
                GasNeuron second = neurons.get(0);
                for (GasNeuron neuron : neurons) {
                    final Double euclid = euclid(row, neuron.getWeights());
                    if (euclid < min) {
                        min = euclid;
                        first = neuron;
                    } else if (euclid < minSecond) {
                        minSecond = euclid;
                        second = neuron;
                    }
                }
                updateErrors(row, first);
                first.setWeights(updateWeight(row, first.getWeights(), nuWinner));
                linkMap.allByKey(first)
                       .forEach(neuron -> neuron.setWeights(updateWeight(row, neuron.getWeights(), nuNeighbour)));
                linkMap.incAllByKey(first);
                linkMap.addToKey(first, second);
                linkMap.removeByKeyAndAge(first, maxAge);
                final int size = neurons.size();
                neurons.removeAll(linkMap.checkNeurons(Lists.newArrayList(neurons)));
                if (size > neurons.size()) {
                    System.out.println();
                }
                if (iteration % lambda == 0 && neurons.size() <= maxNeuronsCount) {
                    final GasNeuron neuronMax = neurons.stream().sorted((o1, o2) -> Double.compare(o2.getError(), o1.getError())).findFirst().get();
                    final GasNeuron secondMax = linkMap.allByKey(neuronMax).stream().sorted((o1, o2) -> Double.compare(o2.getError(), o1.getError())).findFirst().get();
                    final GasNeuron newNeuron = new GasNeuron().setWeights(newWeight(neuronMax.getWeights(), secondMax.getWeights()));
                    neurons.add(newNeuron);
                    linkMap.addToKey(neuronMax, newNeuron);
                    linkMap.addToKey(secondMax, newNeuron);
                    linkMap.removeLink(neuronMax, secondMax);
                    neuronMax.setError(neuronMax.getError()*alpha);
                    secondMax.setError(secondMax.getError()*alpha);
                    newNeuron.setError((neuronMax.getError()+secondMax.getError())/2);
                }
                iteration++;
            }
        }
        System.out.println();

        final DoubleColumn xColumn = DoubleColumn.create("x", neurons.stream()
                                                                 .mapToDouble(neuron -> neuron.getWeights().get(0)));

        final DoubleColumn yColumn = DoubleColumn.create("y", neurons.stream()
                                                                 .mapToDouble(neuron -> neuron.getWeights().get(1)));

        final Table result = Table.create(xColumn, yColumn);
        Plot.show(
        ScatterPlot.create("plot", result, "x", "y"));
        System.out.println();
    }

    private static void updateErrors(List<Double> row, GasNeuron neuron) {
        neuron.setError(neuron.getError() + euclid(row, neuron.getWeights()));
    }

    private static GasNeuron createNeuron(int weightSize) {
        final RandomDataGenerator generator = new RandomDataGenerator();
        final ArrayList<Double> weights = new ArrayList<>(weightSize);
        for (int i = 0; i < weightSize; i++) {
            weights.add(generator.nextUniform(0.0, 1.0));
        }

        return new GasNeuron().setWeights(weights);
    }

    public static Double euclid(List<Double> val1, List<Double> val2) {
        Double sum = 0.0;
        for (int i = 0; i < val1.size(); i++) {
            final Double aDouble = (val1.get(i) - val2.get(i));
            sum += aDouble * aDouble;
        }
        return Math.sqrt(sum);
    }

    public static List<Double> updateWeight(List<Double> row, List<Double> weights, Double coeff) {
        final ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
            final Double weight = weights.get(i);
            result.add(weight + coeff * (row.get(i) - weight));
        }
        return result;
    }

    public static List<Double> newWeight(List<Double> first, List<Double> second) {
        final ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            result.add((first.get(i) + second.get(i))/2);
        }
        return result;
    }
}
