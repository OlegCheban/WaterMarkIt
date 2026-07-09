package com.markit.api;

import com.markit.api.formats.image.WatermarkImageService;
import com.markit.api.formats.pdf.WatermarkPDFService;
import com.markit.api.formats.video.WatermarkVideoService;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * Watermark Service for applying watermarks to different file types.
 *
 * @author Oleg Cheban
 * @since 1.0
 */
public interface WatermarkService {

    static FileFormatSelector create() {
        return new DefaultWatermarkService();
    }

    static FileFormatSelector create(Executor executor) {
        Objects.requireNonNull(executor, "executor is required");
        return new DefaultWatermarkService(executor);
    }

    /**
     * Selector that provides a watermarking service for a specific file
     */
    interface FileFormatSelector {

        /**
         * Sets the PDF file to be watermarked using a byte array.
         */
        WatermarkPDFService watermarkPDF(byte[] fileBytes);

        /**
         * Sets the PDF file to be watermarked using a File object.
         */
        WatermarkPDFService watermarkPDF(File file);

        /**
         * Sets the PDF file to be watermarked using a PDDocument pdfbox object.
         *
         * @param document The PDF document to be watermarked.
         * @see PDDocument
         */
        WatermarkPDFService watermarkPDF(PDDocument document);

        /**
         * @param file The image file to be watermarked.
         */
        WatermarkImageService watermarkImage(File file);

        /**
         * @param fileBytes The byte array representing the source image file.
         */
        WatermarkImageService watermarkImage(byte[] fileBytes);

        /**
         * Sets the video file to be watermarked using a byte array.
         */
        WatermarkVideoService watermarkVideo(byte[] fileBytes);

        /**
         * Sets the video file to be watermarked using a File object.
         */
        WatermarkVideoService watermarkVideo(File file);

        /**
         * Embeds hidden traceability data into a PDF document using a content stream comment.
         * The data is invisible during rendering and survives most PDF editors and optimizers.
         *
         * @param file the PDF file to embed data into
         * @param data the raw bytes to hide (e.g. a user ID, timestamp, hash)
         * @return the modified PDF as a byte array
         * @throws IOException if the file cannot be read or written
         */
        byte[] embedHiddenData(File file, byte[] data) throws IOException;

        /**
         * Extracts hidden traceability data previously embedded by {@link #embedHiddenData}.
         *
         * @param file the PDF file to inspect
         * @return the embedded bytes, or {@code null} if no hidden data is found
         * @throws IOException if the file cannot be read
         */
        byte[] extractHiddenData(File file) throws IOException;
    }
}
