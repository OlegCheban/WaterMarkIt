package com.markit.api;

import com.markit.api.formats.image.WatermarkImageService;
import com.markit.api.formats.pdf.WatermarkPDFService;
import com.markit.api.formats.video.WatermarkVideoService;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
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
    }
}
