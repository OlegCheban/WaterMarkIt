package com.markit.image;

import com.markit.exceptions.ConvertBufferedImageToBytesException;
import com.markit.exceptions.ConvertBytesToBufferedImageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Oleg Cheban
 * @since 1.0
 */
public class ImageConverter {
    private final String ERR_MSG = "I/O error during image conversion";

    public BufferedImage convertToBufferedImage(byte[] imageBytes) {
        return convert(() -> {
            try {
                return ImageIO.read(new ByteArrayInputStream(imageBytes));
            } catch (IOException e) {
                throw new ConvertBytesToBufferedImageException(ERR_MSG);
            }
        });
    }

    public BufferedImage convertToBufferedImage(File file) {
        return convert(() -> {
            try {
                return ImageIO.read(file);
            } catch (IOException e) {
                throw new ConvertBytesToBufferedImageException(ERR_MSG);
            }
        });
    }

    private BufferedImage convert(Supplier<BufferedImage> imageSupplier) {
        return Optional.ofNullable(imageSupplier.get())
                .orElseThrow(() -> new ConvertBytesToBufferedImageException("Failed to convert image bytes to BufferedImage"));
    }

    public byte[] convertToByteArray(BufferedImage image, String imageType) {
        var baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, imageType, baos);
        } catch (IOException e) {
            throw new ConvertBufferedImageToBytesException(ERR_MSG);
        }
        return baos.toByteArray();
    }
}
