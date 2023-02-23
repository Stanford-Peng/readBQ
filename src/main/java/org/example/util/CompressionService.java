package org.example.util;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

@Slf4j
public class CompressionService {
    private static final int DEFAULT_MAX_SIZE = 50_000_000;
    private final int maxSize;

    public CompressionService(int maxSize) {
        this.maxSize = maxSize;
    }

    public CompressionService() {
        this.maxSize = DEFAULT_MAX_SIZE;
    }

    public byte[] compressToGZIP(String input) {
        checkArgument(nonNull(input), "Compression of null input failed");

        final byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        if (inputBytes.length > maxSize) {
            final String message = String.format("Input size %d for compression exceeds the maximum allowed size %d", inputBytes.length, maxSize);
            log.error(message);
            throw new CompressionServiceException(message);
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream outputStream = new GZIPOutputStream(byteStream)) {
            outputStream.write(inputBytes);
        } catch (IOException ex) {
            throw new CompressionServiceException("Compression of input failed", ex);
        }

        return byteStream.toByteArray();
    }

    public String decompressPayload(byte[] payload) {
        checkArgument(nonNull(payload), "Decompression of null input failed");

        if (payload.length > maxSize) {
            final String message = String.format("Input size %d for decompression exceeds the maximum allowed size %d", payload.length, maxSize);
            log.error(message);
            throw new CompressionServiceException(message);
        }
        try {
            final int readSize = 65536;
            try(var inputStream = new GZIPInputStream(new ByteArrayInputStream(payload), readSize);
                var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), readSize)){

                char [] chars = new char[readSize];
                int charsRead = reader.read(chars, 0, readSize);
                long byteCount = 0;

                StringBuilder fulltext = new StringBuilder(payload.length);
                while (charsRead!= -1){
                    fulltext.append(chars, 0, charsRead);
                    byteCount += charsRead;
                    if (byteCount >= maxSize) {
                        final String message = String.format("Decompressed input exceeds the maximum allowed size %d", maxSize);
                        log.error(message);
                        throw new CompressionServiceException(message);
                    }
                    charsRead = reader.read(chars, 0, chars.length);
                }
                return fulltext.toString();
            }

        } catch (IOException e) {
            throw new CompressionServiceException(e);
        }
    }
}
