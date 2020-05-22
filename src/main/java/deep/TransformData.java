package deep;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.records.writer.impl.csv.CSVRecordWriter;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.partition.NumberOfRecordsPartitioner;
import org.datavec.api.split.partition.Partitioner;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.condition.column.LongColumnCondition;
import org.datavec.api.transform.filter.ConditionFilter;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.LocalTransformExecutor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TransformData {

    public static void main(String[] args) throws Exception {
        Schema inputDataSchema = new Schema.Builder()
                .addColumnsLong("sex", "institute", "category",
                        "friends", "followers", "photos", "videos", "pages", "label")
                .build();

        TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
                .filter(new ConditionFilter(
                        new LongColumnCondition("friends", ConditionOp.GreaterThan, 650)))
                .filter(new ConditionFilter(
                        new LongColumnCondition("followers", ConditionOp.GreaterThan, 850)))
                .filter(new ConditionFilter(
                        new LongColumnCondition("photos", ConditionOp.GreaterThan, 350)))
                .filter(new ConditionFilter(
                        new LongColumnCondition("videos", ConditionOp.GreaterThan, 950)))
                .filter(new ConditionFilter(
                        new LongColumnCondition("pages", ConditionOp.GreaterThan, 600)))
                .removeColumns("institute")
                .build();

        RecordReader rr = new CSVRecordReader(1, ';');
        rr.initialize(new FileSplit(new File("/Users/cheparinv/Documents/dl4j", "perceptron.csv")));

        Path resourceDirectory = Paths.get("src", "main", "resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "/transformed.csv";
        final File outputFile = new File(absolutePath);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();
        RecordWriter rw = new CSVRecordWriter();
        Partitioner p = new NumberOfRecordsPartitioner();
        rw.initialize(new FileSplit(outputFile), p);

        //Process the data:
        List<List<Writable>> originalData = new ArrayList<>();
        while (rr.hasNext()) {
            originalData.add(rr.next());
        }

        List<List<Writable>> processedData = LocalTransformExecutor.execute(originalData, tp);
        rw.writeBatch(processedData);
        rw.close();
    }
}
