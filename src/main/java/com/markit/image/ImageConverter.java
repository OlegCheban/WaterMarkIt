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
import javax.imageio.spi.ImageWriterSpi;
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

    /**
     * Convertit un tableau d'octets en BufferedImage.
     *
     * @param imageBytes Le tableau d'octets contenant l'image à convertir
     * @return L'image convertie sous forme de BufferedImage
     * @throws ConvertBytesToBufferedImageException Si la conversion échoue
     */
    public BufferedImage convertToBufferedImage(byte[] imageBytes) {
        return convert(() -> {
            try {
                return ImageIO.read(new ByteArrayInputStream(imageBytes));
            } catch (IOException e) {
                throw new ConvertBytesToBufferedImageException(ERR_MSG);
            }
        });
    }

    /**
     * Convertit un fichier image en BufferedImage.
     *
     * @param file Le fichier image à convertir
     * @return L'image convertie sous forme de BufferedImage
     * @throws ConvertBytesToBufferedImageException Si la conversion échoue
     */
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

    /**
     * Convertit un BufferedImage en tableau d'octets.
     *
     * @param image     L'image à convertir
     * @param imageType Le type d'image cible (PNG, JPEG, etc.)
     * @return Le tableau d'octets contenant l'image convertie
     * @throws ConvertBufferedImageToBytesException Si la conversion échoue
     */
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
            String formatName = detectFormatName(iis);
            validateImageType(formatName);
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
            String formatName = detectFormatName(iis);
            validateImageType(formatName);
            return mapFormatToImageType(formatName);
        } catch (IOException e) {
            throw new ConvertBytesToBufferedImageException("Erreur lors de la détection du type d'image");
        }
    }

    /**
     * Détecte le format d'image à partir d'un flux d'entrée.
     *
     * @param iis Le flux d'entrée d'image
     * @return Le nom du format détecté
     * @throws ConvertBytesToBufferedImageException Si le format ne peut pas être
     *                                              détecté
     */
    private String detectFormatName(ImageInputStream iis) {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        if (!readers.hasNext()) {
            throw new ConvertBytesToBufferedImageException("Format d'image non supporté");
        }
        return readers.next().getFormatName().toLowerCase();
    }

    /**
     * Valide qu'un format d'image est supporté.
     *
     * @param formatName Le nom du format à valider
     * @throws ConvertBytesToBufferedImageException Si le format n'est pas supporté
     */
    private void validateImageType(String formatName) {
        Iterator<ImageWriterSpi> writers = ImageIO.getImageWritersByFormatName(formatName);
        if (!writers.hasNext()) {
            throw new ConvertBytesToBufferedImageException("Format d'image non supporté : " + formatName);
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
