package com.markit.api;

import com.markit.api.formats.image.WatermarkImageService;
import com.markit.api.formats.pdf.WatermarkPDFService;
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
         * Sets the image file to be watermarked using a byte array.
         *
         * @param fileBytes The byte array representing the source file.
         * @param imageType The type of image
         * @see ImageType
         */
        WatermarkImageService watermarkImage(byte[] fileBytes, ImageType imageType);

        /**
         * Sets the image file to be watermarked using a File object.
         *
         * @param file The file to be watermarked.
         * @param imageType The type of image
         */
        WatermarkImageService watermarkImage(File file, ImageType imageType);

        /**
         *
         * @param file The file to be watermarked.
         */
        WatermarkImageService watermarkImage(File file);

        /**
         *
         * @param fileBytes The byte array representing the source file.
         */
        WatermarkImageService watermarkImage(byte[] fileBytes);
    }
}
