package som;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DataProcessing {

    public Table addClassColumnToTable(Table table, Double classValue) {
        final DoubleColumn classColumn =
                DoubleColumn.create("class", Collections.nCopies(table.rowCount(), classValue));
        table.addColumns(classColumn);
        return table;
    }

    public Table readTable(String path, String... columns) throws IOException {
        Table t = Table.read().usingOptions(CsvReadOptions.builder(path)
                                                          .header(true)
                                                          .separator(',')
                                                          .build());
        return Table.create(t.columns(columns));
    }

    public Table normalizedTable(Table table) {
        final NumericColumn<?>[] numericColumns = table.numberColumns();
        final ArrayList<Column<?>> doubleColumns = new ArrayList<>();
        for (NumericColumn<?> numericColumn : numericColumns) {
            if (numericColumn.name().equals("class")) {
                doubleColumns.add(numericColumn.asDoubleColumn());
                continue;
            }
            final DoubleColumn doubles = numericColumn.asDoubleColumn();
            final double max = doubles.max();
            final double min = doubles.min();
            final double diff = max - min;
            doubleColumns.add(doubles.map(aDouble -> (aDouble - min) / diff));
        }
        return Table.create(doubleColumns);
    }

    public List<List<Double>> tableToListOfVectors(Table table) {
        final List<String> columnNames = table.columnNames();
        return table.stream()
                    .map(row -> {
                        final ArrayList<Double> newRow = new ArrayList<>();
                        for (String columnName : columnNames) {
                            newRow.add(row.getDouble(columnName));
                        }
                        return newRow;
                    }).collect(Collectors.toList());
    }
}
