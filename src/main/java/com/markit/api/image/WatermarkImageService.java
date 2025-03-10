package com.markit.api.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

import com.markit.api.positioning.WatermarkPosition;

/**
 * Watermark Service for applying watermarks to images
 *
 * @author Oleg Cheban
 * @since 1.3.0
 */
public interface WatermarkImageService {

    /**
     * Sets the text to be used as the watermark
     *
     * @param text The text for the watermark
     */
    TextBasedWatermarkBuilder withText(String text);

    /**
     * Sets the image to be used as the watermark
     * @param image the byte array representation of the image
     */
    WatermarkImageBuilder withImage(byte[] image);

    /**
     * Sets the image to be used as the watermark
     * @param image the BufferedImage representation of the image
     * @see BufferedImage
     */
    WatermarkImageBuilder withImage(BufferedImage image);

    /**
     * Sets the image to be used as the watermark
     * @param image the File object representing the image
     */
    WatermarkImageBuilder withImage(File image);

    /**
     * Text-based watermarks builder
     */
    interface TextBasedWatermarkBuilder {
        /**
         * Sets the color of the text
         *
         * @param color The color for the text
         * @see Color
         */
        TextBasedWatermarkBuilder color(Color color);

        /**
         * Adds a trademark symbol to the text
         */
        TextBasedWatermarkBuilder addTrademark();

        /**
         * Getting watermarks builder
         */
        WatermarkImageBuilder end();

        /**
         * Applies the watermark to the file and returns the result as a byte array
         *
         * @return A byte array representing the watermarked file
         */
        byte[] apply();
    }

    /**
     * The general image watermarks builder
     */
    interface WatermarkImageBuilder {

        /**
         * Sets the size of the watermark
         */
        WatermarkImageBuilder size(int size);

        /**
         * Sets the opacity of the watermark
         */
        WatermarkImageBuilder opacity(float opacity);

        /**
         * Sets the rotation of the watermark
         */
        WatermarkImageBuilder rotation(int degree);

        /**
         * Defines the position of the watermark on the file
         *
         * @param watermarkPosition The position to place the watermark (e.g., CENTER, CORNER)
         * @see WatermarkPosition
         */
        WatermarkPositionStepBuilder position(WatermarkPosition watermarkPosition);

        /**
         * Defines the position of the watermark on the file directly with x and y coordinates
         * 
         * @param x The direct x-axis offset of watermark
         * @param y The direct y-axis offset of watermark
         */
        WatermarkPositionStepBuilder position(int x, int y);

        /**
         * Enables or disables the watermark based on a specific condition
         *
         * @param condition: A boolean value that determines whether the watermark is enabled (true) or disabled (false)
         */
        WatermarkImageBuilder when(boolean condition);

        /**
         * Adds another watermark configuration to the file.
         *
         * @return A new instance of Watermark for configuring another watermark.
         */
        WatermarkImageService and();

        /**
         * Applies the watermark to the file and returns the result as a byte array
         *
         * @return A byte array representing the watermarked file
         */
        byte[] apply();

        /**
         * Applies a watermark to the file and returns the result as a file path
         * The method generates a watermarked file saved in the specified directory
         * and file name, and provides the file's path for further processing
         *
         * @param directoryPath The directory path where the watermarked file will be saved
         * @param fileName      The name of the watermarked file to be created
         * @return The {@link Path} representing the location of the saved watermarked file
         */
        Path apply(String directoryPath, String fileName);
    }

    /**
     * Interface for adjusting the position of watermarks
     */
    interface WatermarkPositionStepBuilder {
        /**
         * Adjusts the position of the watermark relative to its default location
         *
         * @param x The horizontal offset in pixels
         * @param y The vertical offset in pixels
         */
        WatermarkPositionStepBuilder adjust(int x, int y);

        /**
         * Sets the vertical spacing between multiple tiled watermarks on the page.
         * This is only relevant when the watermark is tiled.
         *
         * @param spacing The spacing between tiles in pixels along the vertical axis.
         *                A larger value increases the distance between adjacent watermarks vertically.
         * @return The current instance of {@code WatermarkPDFBuilder} for method chaining.
         */
        WatermarkPositionStepBuilder verticalSpacing(int spacing);

        /**
         * Sets the horizontal spacing between multiple tiled watermarks on the page.
         * This is only relevant when the watermark is tiled.
         *
         * @param spacing The spacing between tiles in pixels along the horizontal axis.
         *                A larger value increases the distance between adjacent watermarks horizontally.
         * @return The current instance of {@code WatermarkPDFBuilder} for method chaining.
         */
        WatermarkPositionStepBuilder horizontalSpacing(int spacing);

        /**
         * Finish working with WatermarkPositionStepBuilder and back to WatermarkImageBuilder
         */
        WatermarkImageBuilder end();
    }
}
