package com.markit.api;

import com.markit.api.formats.audio.AudioWatermarkContentStep;
import com.markit.api.formats.image.ImageWatermarkContentStep;
import com.markit.api.formats.pdf.PdfWatermarkContentStep;
import com.markit.api.formats.video.VideoWatermarkContentStep;
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
        PdfWatermarkContentStep watermarkPDF(byte[] fileBytes);

        /**
         * Sets the PDF file to be watermarked using a File object.
         */
        PdfWatermarkContentStep watermarkPDF(File file);

        /**
         * Sets the PDF file to be watermarked using a PDDocument pdfbox object.
         *
         * @param document The PDF document to be watermarked.
         * @see PDDocument
         */
        PdfWatermarkContentStep watermarkPDF(PDDocument document);

        /**
         * @param file The image file to be watermarked.
         */
        ImageWatermarkContentStep watermarkImage(File file);

        /**
         * @param fileBytes The byte array representing the source image file.
         */
        ImageWatermarkContentStep watermarkImage(byte[] fileBytes);

        /**
         * Sets the video file to be watermarked using a byte array.
         */
        VideoWatermarkContentStep watermarkVideo(byte[] fileBytes);

        /**
         * Sets the video file to be watermarked using a File object.
         */
        VideoWatermarkContentStep watermarkVideo(File file);

        /**
         * Sets the audio file to receive audible watermarks.
         *
         * @param fileBytes encoded source audio bytes
         * @return the audio watermark DSL
         */
        AudioWatermarkContentStep watermarkAudio(byte[] fileBytes);

        /**
         * Sets the audio file to receive audible watermarks.
         *
         * @param file source audio file
         * @return the audio watermark DSL
         */
        AudioWatermarkContentStep watermarkAudio(File file);
    }
}
