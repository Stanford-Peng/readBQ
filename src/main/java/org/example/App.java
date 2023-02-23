package org.example;

/**
 * Hello world!
 *
 */
import com.google.api.services.bigquery.model.TableRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.options.Description;
import org.example.httpio.HttpWriter;

@Slf4j
public class App
{
    public interface Options extends DataflowPipelineOptions {
        @Description(
                "BigQuery table to read from, specified as "
                        + "<project_id>:<dataset_id>.<table_id>. The dataset must already exist.")
        @Validation.Required
        String getInput();

        void setInput(String value);

        @Description(
                "REST Url destination"
                        + "https://example.com. The REST endpoint must already exist.")
        @Validation.Required
        @Default.String("https://reqbin.com/sample/post/json")
        String getUrl();

        void setUrl(String value);

    }

    public static void main( String[] args )
    {
        System.out.println( "Start to read from BQ and process" );
        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        Pipeline p = Pipeline.create(options);
        PCollection<TableRow> rowsFromBigQuery = readFromBQTable(p, options.getInput());
        System.out.println(options.getUrl());
        rowsFromBigQuery.apply(
                "Send to rest endpoint", new HttpWriter<>(options.getUrl())
        );

        p.run().waitUntilFinish();
    }


    public static PCollection<TableRow> readFromBQTable(Pipeline pipeline,
            String input) {

        // String project = "my-project-id";
        // String dataset = "my_bigquery_dataset_id";
        // String table = "my_bigquery_table_id";

        // Pipeline pipeline = Pipeline.create();

        PCollection<TableRow> rows =
                pipeline
                        .apply(
                                "Read from BigQuery query",
                                BigQueryIO.readTableRows().from(input));

        return rows;
    }



}
