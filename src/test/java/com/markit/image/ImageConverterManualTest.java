package com.markit.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.markit.api.ImageType;
import com.markit.exceptions.ConvertBytesToBufferedImageException;

/**
 * Test manuel pour la détection du type d'image.
 * Pour exécuter ce test :
 * 1. Créez un fichier test.png dans src/test/resources
 * 2. Exécutez la méthode main
 */
public class ImageConverterManualTest {
    public static void main(String[] args) {
        try {
            // Créer une image de test
            BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            File testFile = new File("src/test/resources/test.png");
            testFile.getParentFile().mkdirs();
            ImageIO.write(testImage, "PNG", testFile);

            // Tester la détection
            ImageConverter converter = new ImageConverter();
            ImageType detectedType = converter.detectImageType(testFile);
            System.out.println("Type d'image détecté : " + detectedType);

            // Nettoyer
            testFile.delete();
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