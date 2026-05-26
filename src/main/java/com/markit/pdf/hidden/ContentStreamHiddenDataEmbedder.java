package com.markit.pdf.hidden;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * Embeds hidden data as a PDF comment inside the first page's content stream.
 */
public class ContentStreamHiddenDataEmbedder implements HiddenDataEmbedder {

    static final String MARKER = "%PDFWF-";
    private static final String MARKER_END = "-WF";
    private static final int BUFFER_SIZE = 4096;

    @Override
    public void embed(PDDocument document, byte[] data) throws IOException {
        if (document == null) {
            throw new IllegalArgumentException("Document must not be null");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data must not be null or empty");
        }

        PDPage page = document.getPage(0);
        String encoded = Base64.getEncoder().encodeToString(data);

        byte[] commentBytes = ("\n" + MARKER + encoded + MARKER_END + "\n").getBytes();

        byte[] existing = readPageContent(page);

        byte[] combined = concat(existing, commentBytes);

        PDStream newStream = new PDStream(document);
        try (OutputStream out = newStream.createOutputStream()) {
            out.write(combined);
        }
        page.setContents(newStream);
    }

    @Override
    public byte[] extract(PDDocument document) throws IOException {
        if (document == null) {
            throw new IllegalArgumentException("Document must not be null");
        }

        PDPage page = document.getPage(0);
        String content = new String(readPageContent(page));

        int markerStart = content.indexOf(MARKER);
        if (markerStart < 0) {
            return null;
        }

        int dataStart = markerStart + MARKER.length();
        int dataEnd = content.indexOf(MARKER_END, dataStart);
        if (dataEnd < 0) {
            return null;
        }

        String encoded = content.substring(dataStart, dataEnd).trim();
        return Base64.getDecoder().decode(encoded);
    }

    private byte[] readPageContent(PDPage page) throws IOException {
        try (InputStream is = page.getContents();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            if (is == null) {
                return new byte[0];
            }
            byte[] chunk = new byte[BUFFER_SIZE];
            int read;
            while ((read = is.read(chunk)) != -1) {
                buffer.write(chunk, 0, read);
            }
            return buffer.toByteArray();
        }
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}