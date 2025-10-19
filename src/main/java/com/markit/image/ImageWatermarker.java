package com.markit.image;

import com.markit.api.WatermarkAttributes;
import com.markit.servicelocator.Prioritizable;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface for adding watermarks to images.
 *
 * @author Oleg Cheban
 * @since 1.0
 */
public interface ImageWatermarker extends Prioritizable {

    /**
     * Adds a text watermark to the given image.
     *
     * @param sourceImageBytes The image in byte array format.
     * @param attrs The attributes of watermark
     * @return A byte array representing the watermarked image.
     */
    byte[] watermark(byte[] sourceImageBytes, List<WatermarkAttributes> attrs) throws IOException;

    /**
     * Adds a text watermark to the given image.
     *
     * @param file The source file of image.
     * @param attrs The attributes of watermark
     * @return A byte array representing the watermarked image.
     */
    byte[] watermark(File file, List<WatermarkAttributes> attrs) throws IOException;
}
