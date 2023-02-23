package org.example.util;

public class CompressionServiceException extends RuntimeException {
    public CompressionServiceException() {
    }

    public CompressionServiceException(String message) {
        super(message);
    }

    public CompressionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompressionServiceException(Throwable cause) {
        super(cause);
    }
}
