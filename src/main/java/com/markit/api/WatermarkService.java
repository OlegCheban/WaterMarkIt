package com.markit.api;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * Watermark Service for applying watermarks to different file types.
 *
 * @author Oleg Cheban
 * @since 1.0
 */
public interface WatermarkService {

    static TextBasedFileSetter textBasedWatermarker() {
        return new TextBasedWatermarkServiceImpl();
    }

    static TextBasedFileSetter textBasedWatermarker(Executor executor) {
        Objects.requireNonNull(executor, "executor is required");
        return new TextBasedWatermarkServiceImpl(executor);
    }

    static ImageBasedFileSetter imageBasedWatermarker() {
        return new ImageBasedWatermarkServiceImpl();
    }

    static ImageBasedFileSetter imageBasedWatermarker(Executor executor) {
        Objects.requireNonNull(executor, "executor is required");
        return new ImageBasedWatermarkServiceImpl(executor);
    }

    /**
     * text-based source setter service
     */
    interface TextBasedFileSetter {

        /**
         * Sets the source file to be watermarked using a byte array.
         *
         * @param fileBytes The byte array representing the source file.
         * @param fileType The type of file (e.g., PDF, Image).
         * @see FileType
         */
        TextBasedWatermarker watermark(byte[] fileBytes, FileType fileType);

        /**
         * Sets the source file to be watermarked using a File object.
         *
         * @param file The file to be watermarked.
         * @param fileType The type of file (e.g., PDF, Image).
         */
        TextBasedWatermarker watermark(File file, FileType fileType);

        /**
         * Sets the PDF document to be watermarked.
         *
         * @param document The PDF document to be watermarked.
         */
        TextBasedWatermarker watermark(PDDocument document);
    }

    /**
     * text-based watermarker service
     */
    interface TextBasedWatermarker {
        /**
         * Sets the text to be used as the watermark.
         *
         * @param text The text for the watermark.
         */
        TextBasedWatermarkBuilder withText(String text);
    }

    /**
     * interface for building and applying text-based watermarks
     */
    interface TextBasedWatermarkBuilder {
        /**
         * Sets the size of the watermark text.
         *
         * @param size The font size for the watermark text.
         */
        TextBasedWatermarkBuilder size(int size);

        /**
         * Defines the method for adding a watermark (default is OVERLAY).
         *
         * @param watermarkingMethod The method to use for watermarking.
         * @see WatermarkingMethod
         */
        TextBasedWatermarkBuilder method(WatermarkingMethod watermarkingMethod);

        /**
         * Defines the position of the watermark on the file.
         *
         * @param watermarkPosition The position to place the watermark (e.g., CENTER, CORNER).
         * @see WatermarkPosition
         */
        TextBasedWatermarkPositionStepBuilder position(WatermarkPosition watermarkPosition);

        /**
         * Sets the color of the watermark.
         *
         * @param color The color for the watermark text.
         * @see Color
         */
        TextBasedWatermarkBuilder color(Color color);

        /**
         * Sets the opacity of the watermark.
         *
         * @param opacity The opacity value, ranging from 0.0 (fully transparent) to 1.0 (fully opaque).
         */
        TextBasedWatermarkBuilder opacity(float opacity);

        /**
         * Specifies the resolution for the watermark in DPI.
         *
         * @param dpi The resolution in DPI.
         */
        TextBasedWatermarkBuilder dpi(float dpi);

        /**
         * Adds a trademark symbol to the watermark.
         *
         */
        TextBasedWatermarkBuilder addTrademark();

        /**
         * Changes the rotation of the watermark
         */
        TextBasedWatermarkBuilder rotation(int degree);

        /**
         * Adds another watermark configuration to the file.
         *
         * @return A new instance of Watermark for configuring another watermark.
         */
        TextBasedWatermarker and();

        /**
         * Adds a condition to filter the document when applying the watermark.
         * Only documents that meet the condition will have the watermark applied.
         *
         * @param predicate: A condition that takes a PDDocument as input and returns true/false.
         */
        TextBasedWatermarkBuilder documentFilter(Predicate<PDDocument> predicate);

        /**
         * Adds a condition to filter the page when applying the watermark.
         * Only pages that meet the condition will have the watermark applied.
         *
         * @param predicate A condition that takes a page number (Integer) as input and returns true/false.
         *                  The page index starts from 1.
         */
        TextBasedWatermarkBuilder filterPage(Predicate<Integer> predicate);

        /**
         * Enables or disables the watermark based on a specific condition.
         * @param condition: A boolean value that determines whether the watermark is enabled (true) or disabled (false).
         */
        TextBasedWatermarkBuilder when(boolean condition);

        /**
         * Applies the watermark to the file and returns the result as a byte array.
         *
         * @return A byte array representing the watermarked file.
         */
        byte[] apply();
    }

    /**
     * Interface for adjusting the position of text-based watermarks.
     */
    interface TextBasedWatermarkPositionStepBuilder extends TextBasedWatermarkBuilder {

        /**
         * Adjusts the position of the watermark relative to its default location.
         *
         * @param x The horizontal offset in pixels.
         * @param y The vertical offset in pixels.
         */
        TextBasedWatermarkBuilder adjust(int x, int y);
    }

    /**
     * image-based source setter service
     */
    interface ImageBasedFileSetter {

        /**
         * Sets the source file to be watermarked using a byte array.
         *
         * @param fileBytes The byte array representing the source file.
         * @param fileType The type of file (e.g., PDF, Image).
         * @see FileType
         */
        ImageBasedWatermarker watermark(byte[] fileBytes, FileType fileType);

        /**
         * Sets the source file to be watermarked using a File object.
         *
         * @param file The file to be watermarked.
         * @param fileType The type of file (e.g., PDF, Image).
         */
        ImageBasedWatermarker watermark(File file, FileType fileType);

        /**
         * Sets the PDF document to be watermarked.
         *
         * @param document The PDF document to be watermarked.
         */
        ImageBasedWatermarker watermark(PDDocument document);
    }

    /**
     * image-based watermarker service
     */
    interface ImageBasedWatermarker {
        /**
         * Sets the watermark image.
         */
        ImageBasedWatermarkBuilder withImage(byte[] image);
    }

    /**
     * interface for building and applying image-based watermarks
     */
    interface ImageBasedWatermarkBuilder {
        /**
         * Sets the size of the watermark image.
         */
        ImageBasedWatermarkBuilder size(int size);
        /**
         * Sets the opacity of the watermark.
         */
        ImageBasedWatermarkBuilder opacity(float opacity);

        /**
         * Sets the rotation of the watermark.
         */
        ImageBasedWatermarkBuilder rotation(int degree);
        /**
         * Sets the dpi of the watermark.
         */
        ImageBasedWatermarkBuilder dpi(float dpi);
        /**
         * Defines the position of the watermark on the file.
         *
         * @param position The position to place the watermark
         * @see WatermarkPosition
         */
        ImageBasedWatermarkPositionStepBuilder position(WatermarkPosition position);

        /**
         * Adds a condition to filter the document when applying the watermark.
         * Only documents that meet the condition will have the watermark applied.
         *
         * @param predicate: A condition that takes a PDDocument as input and returns true/false.
         */
        ImageBasedWatermarkBuilder documentFilter(Predicate<PDDocument> predicate);

        /**
         * Adds a condition to filter the page when applying the watermark.
         * Only pages that meet the condition will have the watermark applied.
         *
         * @param predicate A condition that takes a page number (Integer) as input and returns true/false.
         *                  The page index starts from 1.
         */
        ImageBasedWatermarkBuilder filterPage(Predicate<Integer> predicate);

        /**
         * Enables or disables the watermark based on a specific condition.
         *
         * @param condition: A boolean value that determines whether the watermark is enabled (true) or disabled (false).
         */
        ImageBasedWatermarkBuilder when(boolean condition);

        /**
         * Applies the watermark to the file and returns the result as a byte array.
         *
         * @return A byte array representing the watermarked file.
         */
        byte[] apply();
    }

    /**
     * Interface for adjusting the position of image-based watermarks.
     */
    interface ImageBasedWatermarkPositionStepBuilder extends ImageBasedWatermarkBuilder {

        /**
         * Adjusts the position of the watermark relative to its default location.
         *
         * @param x The horizontal offset in pixels.
         * @param y The vertical offset in pixels.
         */
        ImageBasedWatermarkBuilder adjust(int x, int y);
    }
}
