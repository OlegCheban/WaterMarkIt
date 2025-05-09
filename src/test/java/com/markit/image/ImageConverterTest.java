package com.markit.image;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.markit.api.ImageType;
import com.markit.exceptions.ConvertBytesToBufferedImageException;

class ImageConverterTest {
    private ImageConverter imageConverter;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        imageConverter = new ImageConverter();
        testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    }

    @Test
    void detectImageType_shouldDetectPNG_fromBytes() throws IOException {
        // Given
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();

        // When
        ImageType detectedType = imageConverter.detectImageType(imageBytes);

        // Then
        assertEquals(ImageType.PNG, detectedType);
    }

    @Test
    void detectImageType_shouldDetectJPEG_fromBytes() throws IOException {
        // Given
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "JPEG", baos);
        byte[] imageBytes = baos.toByteArray();

        // When
        ImageType detectedType = imageConverter.detectImageType(imageBytes);

        // Then
        assertEquals(ImageType.JPEG, detectedType);
    }

    @Test
    void detectImageType_shouldThrowException_forUnsupportedFormat() {
        // Given
        byte[] invalidImageBytes = "Not an image".getBytes();

        // When & Then
        assertThrows(ConvertBytesToBufferedImageException.class,
                () -> imageConverter.detectImageType(invalidImageBytes));
    }

    @Test
    void detectImageType_shouldDetectPNG_fromFile() throws IOException {
        // Given
        File tempFile = File.createTempFile("test", ".png");
        tempFile.deleteOnExit();
        ImageIO.write(testImage, "PNG", tempFile);

        // When
        ImageType detectedType = imageConverter.detectImageType(tempFile);

        // Then
        assertEquals(ImageType.PNG, detectedType);
    }

    @Test
    void detectImageType_shouldDetectJPEG_fromFile() throws IOException {
        // Given
        File tempFile = File.createTempFile("test", ".jpg");
        tempFile.deleteOnExit();
        ImageIO.write(testImage, "JPEG", tempFile);

        // When
        ImageType detectedType = imageConverter.detectImageType(tempFile);

        // Then
        assertEquals(ImageType.JPEG, detectedType);
    }

    @Test
    void detectImageType_shouldThrowException_forInvalidFile() {
        // Given
        File invalidFile = new File("nonexistent.png");

        // When & Then
        assertThrows(ConvertBytesToBufferedImageException.class,
                () -> imageConverter.detectImageType(invalidFile));
    }
}