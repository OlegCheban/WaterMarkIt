package com.markit.api;

import com.markit.api.handlers.WatermarkHandler;
import com.markit.api.handlers.WatermarksHandler;
import com.markit.exceptions.WatermarkingException;
import com.markit.image.ImageConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * @author Oleg Cheban
 * @since 1.2.0
 */
public class ImageBasedWatermarkServiceImpl implements WatermarkService.ImageBasedWatermarker, WatermarkService.ImageBasedWatermarkBuilder {
    private static final Log logger = LogFactory.getLog(ImageBasedWatermarkServiceImpl.class);
    private Executor executor;
    private WatermarkAttributes watermarkAttributes;
    private WatermarkHandler watermarkHandler;
    private ImageConverter imageConverter;

    public ImageBasedWatermarkServiceImpl() {
    }

    public ImageBasedWatermarkServiceImpl(Executor e) {
        this.executor = e;
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder watermark(byte[] fileBytes, FileType ft) {
        return configureDefaultParams(new WatermarksHandler().getHandler(fileBytes, ft, this.executor));
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder watermark(File file, FileType ft) {
        return configureDefaultParams(new WatermarksHandler().getHandler(file, ft, this.executor));
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder watermark(PDDocument document) {
        return configureDefaultParams(new WatermarksHandler().getHandler(document, FileType.PDF, this.executor));
    }

    private WatermarkService.ImageBasedWatermarkBuilder configureDefaultParams(WatermarkHandler h) {
        imageConverter = new ImageConverter();
        watermarkAttributes = new WatermarkAttributes();
        watermarkAttributes.setMethod(WatermarkingMethod.DRAW);
        this.watermarkHandler = h;
        return this;
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder withImage(byte[] image) {
        var convertedImage = imageConverter.convertToBufferedImage(image);
        watermarkAttributes.setImage(Optional.of(convertedImage));
        return this;
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder size(int size) {
        watermarkAttributes.setSize(size);
        return this;
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder opacity(float opacity) {
        watermarkAttributes.setOpacity(opacity);
        return this;
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder rotation(int degree) {
        watermarkAttributes.setRotation(degree);
        return this;
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder dpi(float dpi) {
        watermarkAttributes.setDpi(dpi);
        return this;
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder position(WatermarkPosition position) {
        watermarkAttributes.setPosition(position);
        return this;
    }

    @Override
    public byte[] apply() {
        try {
            return this.watermarkHandler.apply(Collections.singletonList(this.watermarkAttributes));
        } catch (IOException e) {
            logger.error("Failed to watermark file", e);
            throw new WatermarkingException("Error watermarking the file", e);
        }
    }
}
