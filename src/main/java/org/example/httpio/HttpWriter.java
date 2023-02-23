package org.example.httpio;

import com.google.api.services.bigquery.model.TableRow;
import lombok.AllArgsConstructor;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.example.util.CompressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * A DoFn to write to Http.
 */
@AllArgsConstructor
public class HttpWriter<T> extends PTransform<PCollection<TableRow>, PDone> {

    private static final Logger logger = LoggerFactory.getLogger(HttpWriter.class);
    private String url;



//    @Override
//    public PDone expand(PCollection<String> input) {
//        input.apply(ParDo.of(new Fn<>()));
//        return PDone.in(input.getPipeline());
//    }

    @Override
    public PDone expand(PCollection<TableRow> input) {
        System.out.println("URL:"+ url);
        var output = input.apply(ParDo.of(new Fn<>(url)));

        return PDone.in(input.getPipeline());
    }

    @AllArgsConstructor
    private static class Fn<T> extends DoFn<TableRow, String> {

        private HttpClient asyncHttpClient;
        private String url;

        private static final CompressionService compressionService = new CompressionService();

        private Fn(String url) {
            this.url = url;
        }

        @Setup
        public void onSetup() {
            this.asyncHttpClient = new HttpClient();
        }

        @ProcessElement
        public void onElement(final ProcessContext context) {
            final TableRow element = context.element();
            var encoded = element.get("payload").toString();
            byte[] decodedPayload = Base64.getDecoder().decode(encoded);
            String uncompressedPayload = compressionService.decompressPayload(decodedPayload);
            System.out.println("Sending:" + element.get("message_id").toString());
            logger.info("The input in writer: {} to {}", uncompressedPayload, url);
            this.asyncHttpClient.send(uncompressedPayload, url);
        }
    }
}

