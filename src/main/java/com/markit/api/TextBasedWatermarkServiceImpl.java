package com.markit.api;

import com.markit.api.handlers.WatermarkHandler;
import com.markit.api.handlers.WatermarksHandler;
import com.markit.exceptions.EmptyWatermarkTextException;
import com.markit.exceptions.UnsupportedFileTypeException;
import com.markit.exceptions.WatermarkingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * @author Oleg Cheban
 * @since 1.0
 */
public class TextBasedWatermarkServiceImpl implements WatermarkService.TextBasedFileSetter, WatermarkService.TextBasedWatermarker, WatermarkService.TextBasedWatermarkBuilder, WatermarkService.TextBasedWatermarkPositionStepBuilder {
    private static final Log logger = LogFactory.getLog(TextBasedWatermarkServiceImpl.class);
    private FileType fileType;
    private WatermarkHandler watermarkHandler;
    private final List<WatermarkAttributes> watermarks = new ArrayList<>();
    private WatermarkAttributes currentWatermark;
    private Executor executor;

    public TextBasedWatermarkServiceImpl() {
    }

    public TextBasedWatermarkServiceImpl(Executor e) {
        this.executor = e;
    }

    @Override
    public WatermarkService.TextBasedWatermarker watermark(PDDocument document) {
        return configureDefaultParams(FileType.PDF, new WatermarksHandler().getHandler(document, FileType.PDF, this.executor));
    }

    @Override
    public WatermarkService.TextBasedWatermarker watermark(byte[] fileBytes, FileType ft) {
        return configureDefaultParams(ft, new WatermarksHandler().getHandler(fileBytes, ft, this.executor));
    }

    @Override
    public WatermarkService.TextBasedWatermarker watermark(File file, FileType ft) {
        return configureDefaultParams(ft, new WatermarksHandler().getHandler(file, ft, this.executor));
    }

    private WatermarkService.TextBasedWatermarker configureDefaultParams(FileType ft, WatermarkHandler h) {
        currentWatermark = new WatermarkAttributes();
        currentWatermark.setMethod(defineMethodByFileType(ft));
        this.fileType = ft;
        this.watermarkHandler = h;
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder withText(String text) {
        currentWatermark.setText(text);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder size(int size) {
        currentWatermark.setSize(size);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder method(WatermarkingMethod method) {
        currentWatermark.setMethod(method);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkPositionStepBuilder position(WatermarkPosition position) {
        currentWatermark.setPosition(position);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder adjust(int x, int y) {
        WatermarkAdjustment adjustment = new WatermarkAdjustment(x, y);
        currentWatermark.setAdjustment(adjustment);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder color(Color c) {
        currentWatermark.setColor(c);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder opacity(float opacity) {
        currentWatermark.setOpacity(opacity);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder dpi(float d) {
        currentWatermark.setDpi(d);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder addTrademark() {
        currentWatermark.setTrademark(true);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder rotation(int degree) {
        currentWatermark.setRotation(degree);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarker and() {
        if (currentWatermark.getText().isEmpty()) {
            logger.error("the watermarking text is empty");
            throw new EmptyWatermarkTextException("");
        }
        watermarks.add(currentWatermark);
        currentWatermark = new WatermarkAttributes();
        currentWatermark.setMethod(defineMethodByFileType(fileType));
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder documentFilter(Predicate<PDDocument> predicate) {
        currentWatermark.setDocumentPredicates(predicate);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder filterPage(Predicate<Integer> predicate) {
        currentWatermark.setPagePredicate(predicate);
        return this;
    }

    @Override
    public WatermarkService.TextBasedWatermarkBuilder when(boolean condition) {
        currentWatermark.setWatermarkEnabled(condition);
        return this;
    }

    @Override
    public byte[] apply() {
        try {
            and();
            return this.watermarkHandler.apply(this.watermarks);
        } catch (IOException e) {
            logger.error("Failed to watermark file", e);
            throw new WatermarkingException("Error watermarking the file", e);
        }
    }
    
    public byte[] apply(String filepath) {
    	try {
            // Step 1: Call apply() to get the watermarked byte[]
            byte[] watermarkedData = apply();

            // Step 2: Write the byte[] to the specified file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(watermarkedData);
                logger.info("Watermarked file saved at: {}", filePath);
            }
        } catch (IOException e) {
            logger.error("Failed to save watermarked file", e);
            throw new WatermarkingException("Error saving the watermarked file", e);
        }
    	
    }

    private WatermarkingMethod defineMethodByFileType(FileType ft){
        switch (ft){
            case JPEG:
            case PNG:
            case TIFF:
            case BMP: return WatermarkingMethod.DRAW;
            case PDF: return WatermarkingMethod.OVERLAY;
            default: throw new UnsupportedFileTypeException("Unsupported file type: " + ft);
        }
    }
}
