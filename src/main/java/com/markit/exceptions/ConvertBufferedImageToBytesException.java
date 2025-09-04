package com.markit.exceptions;

/**
 * Exception levée lorsqu'une erreur survient lors de la conversion d'un
 * BufferedImage en tableau d'octets.
 */
public class ConvertBufferedImageToBytesException extends RuntimeException {
    public ConvertBufferedImageToBytesException(String message) {
        super(message);
    }

    public ConvertBufferedImageToBytesException(String message, Throwable cause) {
        super(message, cause);
    }
}