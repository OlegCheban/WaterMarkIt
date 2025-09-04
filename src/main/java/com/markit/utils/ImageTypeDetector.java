package com.markit.utils;

import com.markit.api.ImageType;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Iterator;

public class ImageTypeDetector {

    public static ImageType detect(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return detect(is);
        } catch (IOException e) {
            throw new UnsupportedOperationException("Unable to detect image type from file: " + file, e);
        }
    }

    public static ImageType detect(byte[] bytes) {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            return detect(is);
        } catch (IOException e) {
            throw new UnsupportedOperationException("Unable to detect image type from byte[]", e);
        }
    }

    private static ImageType detect(InputStream is) throws IOException {
        Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReaders(ImageIO.createImageInputStream(is));
        if (readers.hasNext()) {
            String formatName = readers.next().getFormatName().toUpperCase();
            return ImageType.valueOf(formatName);
        }
        throw new UnsupportedOperationException("Unsupported or unknown image format");
    }
}

