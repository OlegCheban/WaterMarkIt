package com.markit.api;

import com.markit.api.formats.image.DefaultWatermarkImageBuilder;
import com.markit.api.formats.image.WatermarkImageService;
import com.markit.api.formats.pdf.DefaultWatermarkPDFBuilder;
import com.markit.api.formats.pdf.WatermarkPDFService;
import com.markit.api.formats.video.DefaultWatermarkVideoBuilder;
import com.markit.api.formats.video.WatermarkVideoService;
import com.markit.exceptions.InvalidPDFFileException;
import com.markit.utils.ImageTypeDetector;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * @author Oleg Cheban
 * @since 1.0
 */
public class DefaultWatermarkService implements WatermarkService.FileFormatSelector {

    private Executor executor;

    public DefaultWatermarkService() {
    }

    public DefaultWatermarkService(Executor e) {
        this.executor = e;
    }

    @Override
    public WatermarkPDFService watermarkPDF(byte[] fileBytes) {
        try {
            return new DefaultWatermarkPDFBuilder(PDDocument.load(fileBytes), executor);
        } catch (IOException e) {
            throw new InvalidPDFFileException(e);
        }
    }

    @Override
    public WatermarkPDFService watermarkPDF(File file) {
        try {
            return new DefaultWatermarkPDFBuilder(PDDocument.load(file), executor);
        } catch (IOException e) {
            throw new InvalidPDFFileException(e);
        }
    }

    @Override
    public WatermarkPDFService watermarkPDF(PDDocument document) {
        return new DefaultWatermarkPDFBuilder(document, executor);
    }

    @Override
    public WatermarkImageService watermarkImage(byte[] fileBytes, ImageType imageType) {
        return new DefaultWatermarkImageBuilder(fileBytes, imageType);
    }

    @Override
    public WatermarkImageService watermarkImage(File file, ImageType imageType) {
        return new DefaultWatermarkImageBuilder(file, imageType);
    }

    @Override
    public WatermarkImageService watermarkImage(File file) {
        ImageType imageType = ImageTypeDetector.detect(file);

        // Validate writer support
        if (!ImageIO.getImageWritersByFormatName(imageType.name().toLowerCase()).hasNext()) {
            throw new UnsupportedOperationException("No writer found for " + imageType);
        }

        return new DefaultWatermarkImageBuilder(file, imageType);
    }

    @Override
    public WatermarkImageService watermarkImage(byte[] fileBytes) {
        ImageType imageType = ImageTypeDetector.detect(fileBytes);

        // Validate writer support
        if (!ImageIO.getImageWritersByFormatName(imageType.name().toLowerCase()).hasNext()) {
            throw new UnsupportedOperationException("No writer found for " + imageType);
        }

        return new DefaultWatermarkImageBuilder(fileBytes, imageType);
    public WatermarkVideoService watermarkVideo(byte[] fileBytes) {
        return new DefaultWatermarkVideoBuilder(fileBytes);
    }

    @Override
    public WatermarkVideoService watermarkVideo(File file) {
        return new DefaultWatermarkVideoBuilder(file);
    }
}
