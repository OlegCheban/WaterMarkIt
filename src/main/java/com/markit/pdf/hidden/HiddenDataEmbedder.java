package com.markit.pdf.hidden;

import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.IOException;


public interface HiddenDataEmbedder {

    /**
     * @param document the target PDF document
     * @param data     the raw bytes to embed
     * @throws IOException if the document cannot be modified
     */
    void embed(PDDocument document, byte[] data) throws IOException;

    /**
     * Extracts previously embedded data from the given PDF document
     * @param document the PDF document to inspect
     * @return the embedded bytes, or {@code null} if no hidden data is found
     * @throws IOException if the document cannot be read
     */
    byte[] extract(PDDocument document) throws IOException;
}