package com.markit.exceptions;

/**
 * Exception levée lorsqu'une erreur survient lors de la conversion d'un tableau
 * d'octets en BufferedImage.
 */
public class ConvertBytesToBufferedImageException extends RuntimeException {
    public ConvertBytesToBufferedImageException(String message) {
        super(message);
    }

    public ConvertBytesToBufferedImageException(String message, Throwable cause) {
        super(message, cause);
    }
}