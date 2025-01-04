package com.markit.api;

import com.markit.api.handlers.WatermarkHandler;
import com.markit.api.handlers.WatermarksHandler;
import com.markit.exceptions.ConvertBytesToBufferedImageException;
import com.markit.exceptions.WatermarkingException;
import com.markit.image.ImageConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * @author Oleg Cheban
 * @since 1.2.0
 */
public class ImageBasedWatermarkServiceImpl implements WatermarkService.ImageBasedFileSetter, WatermarkService.ImageBasedWatermarker, WatermarkService.ImageBasedWatermarkBuilder, WatermarkService.ImageBasedWatermarkPositionStepBuilder {
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
    public WatermarkService.ImageBasedWatermarker watermark(byte[] fileBytes, FileType ft) {
        return configureDefaultParams(new WatermarksHandler().getHandler(fileBytes, ft, this.executor));
    }

    @Override
    public WatermarkService.ImageBasedWatermarker watermark(File file, FileType ft) {
        return configureDefaultParams(new WatermarksHandler().getHandler(file, ft, this.executor));
    }

    @Override
    public WatermarkService.ImageBasedWatermarker watermark(PDDocument document) {
        return configureDefaultParams(new WatermarksHandler().getHandler(document, FileType.PDF, this.executor));
    }

    private WatermarkService.ImageBasedWatermarker configureDefaultParams(WatermarkHandler h) {
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
    public WatermarkService.ImageBasedWatermarkPositionStepBuilder position(WatermarkPosition position) {
        watermarkAttributes.setPosition(position);
        return this;
    }

    @Override
    public WatermarkService.ImageBasedWatermarkBuilder adjust(int x, int y) {
        WatermarkAdjustment adjustment = new WatermarkAdjustment(x, y);
        watermarkAttributes.setAdjustment(adjustment);
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

    public Path apply(String directoryPath, String fileName){
        StringBuilder directoryPathBuilder = new StringBuilder();
        for (int i = 0; i < directoryPath.length(); i++) {
            char c = directoryPath.charAt(i);
            if (c == '/' || c == '\\') {
                directoryPathBuilder.append(File.separator);
            } else {
                directoryPathBuilder.append(c);
            }
        }
        directoryPath = directoryPathBuilder.toString();
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            logger.error("Try to watermark file in a directory that does not exist or is not a directory");
            throw new IllegalArgumentException("The directory does not exist or is not a directory.");
        }
        try {

            byte [] file = apply();
            File newFile = new File(directoryPath + fileName);

            return Files.write(newFile.toPath(), file);

        }catch (IOException e){
            logger.error("Failed to watermark file", e);
            throw new WatermarkingException("Error watermarking the file", e);
        }catch (ConvertBytesToBufferedImageException e) {
            logger.error("Failed to convert bytes to buffered image", e);
            throw new WatermarkingException("Error converting bytes to buffered image", e);
        }


    }
}
