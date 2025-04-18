package com.markit.api;

import com.markit.api.builders.PositionStepBuilder;
import com.markit.api.builders.TextBasedWatermarkBuilder;
import com.markit.api.positioning.WatermarkPosition;
import com.markit.api.positioning.WatermarkPositionCoordinates;
import com.markit.exceptions.WatermarkingException;
import com.markit.image.ImageConverter;
import com.markit.utils.ValidationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Oleg Cheban
 * @since 1.3.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractWatermarkService<WatermarkServiceType, WatermarkBuilderType>
        implements PositionStepBuilder<WatermarkBuilderType>, TextBasedWatermarkBuilder<WatermarkBuilderType> {
    private static final Log logger = LogFactory.getLog(AbstractWatermarkService.class);
    protected WatermarkHandler watermarkHandler;
    protected final List<WatermarkAttributes> watermarks = new ArrayList<>();
    protected WatermarkAttributes currentWatermark;

    public AbstractWatermarkService() {
        this.currentWatermark = new WatermarkAttributes();
    }

    public TextBasedWatermarkBuilder<WatermarkBuilderType> withText(String text) {
        Objects.requireNonNull(text);
        currentWatermark.setText(text);
        return this;
    }

    public WatermarkBuilderType withImage(byte[] image) {
        Objects.requireNonNull(image);
        var imageConverter = new ImageConverter();
        return withImage(() -> imageConverter.convertToBufferedImage(image));
    }

    public WatermarkBuilderType withImage(BufferedImage image) {
        Objects.requireNonNull(image);
        return withImage(() -> image);
    }

    public WatermarkBuilderType withImage(File image) {
        Objects.requireNonNull(image);
        var imageConverter = new ImageConverter();
        return withImage(() -> imageConverter.convertToBufferedImage(image));
    }

    private WatermarkBuilderType withImage(Supplier<BufferedImage> imageSupplier) {
        currentWatermark.setImage(Optional.of(imageSupplier.get()));
        return (WatermarkBuilderType) this;
    }

    public TextBasedWatermarkBuilder<WatermarkBuilderType> color(Color color) {
        Objects.requireNonNull(color);
        currentWatermark.setColor(color);
        return this;
    }

    public TextBasedWatermarkBuilder<WatermarkBuilderType> addTrademark() {
        currentWatermark.setTrademark(true);
        return this;
    }

    public WatermarkBuilderType size(int size) {
        currentWatermark.setSize(size);
        return (WatermarkBuilderType) this;
    }

    public WatermarkBuilderType opacity(int opacity) {
        currentWatermark.setOpacity(opacity);
        return (WatermarkBuilderType) this;
    }

    public WatermarkBuilderType rotation(int degree) {
        currentWatermark.setRotationDegrees(degree);
        return (WatermarkBuilderType) this;
    }

    public WatermarkBuilderType enableIf(boolean condition) {
        currentWatermark.setWatermarkEnabled(condition);
        return (WatermarkBuilderType) this;
    }

    public PositionStepBuilder<WatermarkBuilderType> position(WatermarkPosition watermarkPosition) {
        Objects.requireNonNull(watermarkPosition);
        currentWatermark.setPosition(watermarkPosition);
        return this;
    }

    public PositionStepBuilder<WatermarkBuilderType> adjust(int x, int y) {
        var adjustment = new WatermarkPositionCoordinates.Coordinates(x, y);
        currentWatermark.setPositionAdjustment(adjustment);
        return this;
    }

    public PositionStepBuilder<WatermarkBuilderType> verticalSpacing(int spacing) {
        currentWatermark.setVerticalSpacing(spacing);
        return this;
    }

    public PositionStepBuilder<WatermarkBuilderType> horizontalSpacing(int spacing) {
        currentWatermark.setHorizontalSpacing(spacing);
        return this;
    }

    public WatermarkBuilderType end() {
        return (WatermarkBuilderType) this;
    }

    @NotNull
    public byte[] apply() {
        try {
            and();
            return this.watermarkHandler.apply(this.watermarks);
        } catch (IOException e) {
            logger.error("Failed to watermark file", e);
            throw new WatermarkingException("Error watermarking the file", e);
        }
    }

    public WatermarkServiceType and() {
        ValidationUtils.validateWatermarkAttributes(currentWatermark);
        watermarks.add(currentWatermark);
        currentWatermark = new WatermarkAttributes();
        return (WatermarkServiceType) this;
    }
}
