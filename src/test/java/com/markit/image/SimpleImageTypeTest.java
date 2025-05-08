package com.markit.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Test simple pour la détection du type d'image.
 */
public class SimpleImageTypeTest {
    public static void main(String[] args) {
        File testFile = new File("src/test/resources/logo.png");

        try {
            // Test de détection avec ImageIO
            try (ImageInputStream iis = ImageIO.createImageInputStream(testFile)) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                if (readers.hasNext()) {
                    ImageReader reader = readers.next();
                    String formatName = reader.getFormatName();
                    System.out.println("Type d'image détecté : " + formatName);
                } else {
                    System.out.println("Format d'image non supporté");
                }
            }

            // Test de lecture de l'image
            BufferedImage image = ImageIO.read(testFile);
            if (image != null) {
                System.out.println("Image lue avec succès");
                System.out.println("Dimensions : " + image.getWidth() + "x" + image.getHeight());
            } else {
                System.out.println("Échec de la lecture de l'image");
            }

        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}