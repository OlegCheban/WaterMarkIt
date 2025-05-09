package com.markit.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.markit.api.ImageType;
import com.markit.exceptions.ConvertBytesToBufferedImageException;

/**
 * Test manuel pour la détection du type d'image.
 * Pour exécuter ce test :
 * 1. Assurez-vous que le fichier logo.png existe dans src/test/resources
 * 2. Exécutez la méthode main
 */
public class ImageConverterManualTest {
    public static void main(String[] args) {
        try {
            // Utiliser le fichier logo.png existant
            File testFile = new File("src/test/resources/logo.png");
            if (!testFile.exists()) {
                System.err.println("Erreur : Le fichier logo.png n'existe pas dans src/test/resources");
                return;
            }

            // Tester la détection
            ImageConverter converter = new ImageConverter();
            ImageType detectedType = converter.detectImageType(testFile);
            System.out.println("Type d'image détecté : " + detectedType);

            // Tester la conversion en BufferedImage
            BufferedImage image = converter.convertToBufferedImage(testFile);
            System.out.println("Image convertie avec succès : " + image.getWidth() + "x" + image.getHeight());

            // Tester la conversion en bytes
            byte[] imageBytes = converter.convertToByteArray(image, detectedType);
            System.out.println("Image convertie en bytes : " + imageBytes.length + " octets");

            System.out.println("Test réussi !");
        } catch (IOException e) {
            System.err.println("Erreur lors du test : " + e.getMessage());
            e.printStackTrace();
        } catch (ConvertBytesToBufferedImageException e) {
            System.err.println("Erreur de détection : " + e.getMessage());
            e.printStackTrace();
        }
    }
}