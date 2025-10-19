package com.markit.image;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Iterator;

/**
 * @author Krishnanand
 * @since 1.4.2
 */
public class ImageTypeDetector {

    public static String detect(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return detect(is);
        } catch (IOException e) {
            throw new UnsupportedOperationException("Unable to detect image type from file: " + file, e);
        }
    }

    public static String detect(byte[] bytes) {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            return detect(is);
        } catch (IOException e) {
            throw new UnsupportedOperationException("Unable to detect image type from byte[]", e);
        }
    }

    private static String detect(InputStream is) throws IOException {
        Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReaders(ImageIO.createImageInputStream(is));
        if (readers.hasNext()) {
            return readers.next().getFormatName().toUpperCase();
        }
        throw new UnsupportedOperationException("Unsupported or unknown image format");
    }
}

