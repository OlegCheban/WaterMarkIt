package com.markit.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.markit.api.ImageType;
import com.markit.exceptions.ConvertBufferedImageToBytesException;
import com.markit.exceptions.ConvertBytesToBufferedImageException;

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
                .orElseThrow(() -> new ConvertBytesToBufferedImageException(
                        "Failed to convert image bytes to BufferedImage"));
    }

    public byte[] convertToByteArray(BufferedImage image, ImageType imageType) {
        var baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, imageType.name(), baos);
        } catch (IOException e) {
            throw new ConvertBufferedImageToBytesException(ERR_MSG);
        }
        return baos.toByteArray();
    }

    /**
     * Détecte automatiquement le type d'image à partir d'un tableau d'octets.
     *
     * @param imageBytes Le tableau d'octets contenant l'image
     * @return Le type d'image détecté
     * @throws ConvertBytesToBufferedImageException Si le type d'image ne peut pas
     *                                              être détecté
     */
    public ImageType detectImageType(byte[] imageBytes) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imageBytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw new ConvertBytesToBufferedImageException("Format d'image non supporté");
            }
            String formatName = readers.next().getFormatName().toLowerCase();
            return mapFormatToImageType(formatName);
        } catch (IOException e) {
            throw new ConvertBytesToBufferedImageException("Erreur lors de la détection du type d'image");
        }
    }

    /**
     * Détecte automatiquement le type d'image à partir d'un fichier.
     *
     * @param file Le fichier image
     * @return Le type d'image détecté
     * @throws ConvertBytesToBufferedImageException Si le type d'image ne peut pas
     *                                              être détecté
     */
    public ImageType detectImageType(File file) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw new ConvertBytesToBufferedImageException("Format d'image non supporté");
            }
            String formatName = readers.next().getFormatName().toLowerCase();
            return mapFormatToImageType(formatName);
        } catch (IOException e) {
            throw new ConvertBytesToBufferedImageException("Erreur lors de la détection du type d'image");
        }
    }

    /**
     * Convertit un format d'image en type d'image.
     *
     * @param formatName Le nom du format d'image
     * @return Le type d'image correspondant
     * @throws ConvertBytesToBufferedImageException Si le format n'est pas supporté
     */
    private ImageType mapFormatToImageType(String formatName) {
        switch (formatName) {
            case "png":
                return ImageType.PNG;
            case "jpeg":
            case "jpg":
                return ImageType.JPEG;
            case "tiff":
                return ImageType.TIFF;
            case "bmp":
                return ImageType.BMP;
            default:
                throw new ConvertBytesToBufferedImageException("Format d'image non supporté : " + formatName);
        }
    }
}
