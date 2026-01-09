package com.markit.video.ffmpeg.filters;

import com.markit.api.WatermarkAttributes;
import com.markit.api.positioning.Coordinates;
import com.markit.image.WatermarkPositioner;
import com.markit.video.ffmpeg.probes.VideoDimensions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * overlay filter chain builder
 *
 * @author Oleg Cheban
 * @since 1.4.0
 */
public class OverlayFilterStepBuilder implements FilterStepBuilder {
    @Override
    public FilterStepType getFilterStepType() {
        return FilterStepType.OVERLAY;
    }

    @Override
    public FilterStepAttributes build(List<WatermarkAttributes> attrs, VideoDimensions dimensions,
                                      String lastLabel, int step, boolean isEmptyFilter) throws Exception {
        StringBuilder filter = new StringBuilder();
        List<File> tempImages = new ArrayList<>();

        for (WatermarkAttributes attr : attrs) {
            OverlayContext context = processOverlay(attr, dimensions, tempImages, lastLabel, step, isEmptyFilter);
            appendOverlayFilters(filter, context);

            lastLabel = context.lastLabel;
            step = context.step;
            isEmptyFilter = context.isEmptyFilter;
        }

        return new FilterStepAttributes(filter.toString(), lastLabel, step, isEmptyFilter, tempImages);
    }

    private OverlayContext processOverlay(WatermarkAttributes attr, VideoDimensions dimensions,
                                          List<File> tempImages, String lastLabel, int step,
                                          boolean isEmptyFilter) throws Exception {
        BufferedImage originalImage = attr.getImage().get();
        Dimension targetDimensions = calculateTargetDimensions(originalImage, attr.getSize());
        BufferedImage scaledImage = scaleImage(originalImage, targetDimensions);

        // Apply opacity in Java if specified (check if < 100, default is 100)
        if (attr.getOpacity() < 100) {
            scaledImage = applyOpacity(scaledImage, attr.getOpacity());
        }

        List<Coordinates> coordinates = calculateOverlayCoordinates(attr, dimensions, targetDimensions);

        return new OverlayContext(scaledImage, coordinates, tempImages, attr, lastLabel, step, isEmptyFilter);
    }

    private Dimension calculateTargetDimensions(BufferedImage image, int sizePercentage) {
        double scale = sizePercentage / 100.0;
        int width = Math.max(1, (int) Math.round(image.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(image.getHeight() * scale));
        return new Dimension(width, height);
    }

    private BufferedImage scaleImage(BufferedImage original, Dimension targetSize) {
        BufferedImage scaled = createScaledImage(targetSize);
        Graphics2D g2d = scaled.createGraphics();

        try {
            configureGraphics(g2d);
            g2d.drawImage(original, 0, 0, targetSize.width, targetSize.height, null);
        } finally {
            g2d.dispose();
        }

        return scaled;
    }

    private BufferedImage createScaledImage(Dimension size) {
        return new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    }

    private void configureGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    /**
     * Apply opacity to an image by adjusting its alpha channel using AlphaComposite.
     * This method creates a new BufferedImage with the specified opacity applied to all pixels.
     *
     * @param image the original image to apply opacity to
     * @param opacityPercent the desired opacity level (0-100, where 0 is transparent and 100 is opaque)
     * @return a new BufferedImage with the opacity applied
     */
    private BufferedImage applyOpacity(BufferedImage image, int opacityPercent) {
        float alpha = opacityPercent / 100.0f;

        BufferedImage result = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = result.createGraphics();
        try {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(image, 0, 0, null);
        } finally {
            g2d.dispose();
        }

        return result;
    }

    private List<Coordinates> calculateOverlayCoordinates(WatermarkAttributes attr, VideoDimensions dimensions,
                                                          Dimension overlaySize) {
        return WatermarkPositioner.defineXY(
                attr,
                dimensions.getWidth(),
                dimensions.getHeight(),
                overlaySize.width,
                overlaySize.height
        );
    }

    private void appendOverlayFilters(StringBuilder filter, OverlayContext context) throws IOException {
        for (Coordinates coord : context.coordinates) {
            File tempImageFile = saveTempImage(context.scaledImage);
            context.tempImages.add(tempImageFile);

            String overlayFilter = buildOverlayFilter(coord, context.inLabel, context.outLabel,
                    context.tempImages.size());

            if (!context.isEmptyFilter) {
                filter.append(",");
            }
            filter.append(overlayFilter);

            context.advance();
        }
    }

    private File saveTempImage(BufferedImage image) throws IOException {
        File tempFile = Files.createTempFile("wmk-img", ".png").toFile();
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }

    private String buildOverlayFilter(Coordinates coord, String inLabel, String outLabel, int imageIndex) {
        // Opacity is now applied in Java before saving the image,
        // so we always use the simple overlay filter
        return String.format("%s[%d:v]overlay=x=%d:y=%d%s",
                inLabel,
                imageIndex,
                coord.getX(),
                coord.getY(),
                outLabel
        );
    }

    @Override
    public int getPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * Context holder for overlay filter building state
     */
    private static class OverlayContext {
        private final BufferedImage scaledImage;
        private final List<Coordinates> coordinates;
        private final List<File> tempImages;
        private final WatermarkAttributes attr;
        private String lastLabel;
        private int step;
        private boolean isEmptyFilter;
        private String inLabel;
        private String outLabel;

        OverlayContext(BufferedImage scaledImage, List<Coordinates> coordinates, List<File> tempImages,
                       WatermarkAttributes attr, String lastLabel, int step, boolean isEmptyFilter) {
            this.scaledImage = scaledImage;
            this.coordinates = coordinates;
            this.tempImages = tempImages;
            this.attr = attr;
            this.lastLabel = lastLabel;
            this.step = step;
            this.isEmptyFilter = isEmptyFilter;
            updateLabels();
        }

        private void updateLabels() {
            this.inLabel = step == 0 ? "[0:v]" : lastLabel;
            this.outLabel = "[v" + (step + 1) + "]";
        }

        void advance() {
            this.lastLabel = this.outLabel;
            this.isEmptyFilter = false;
            this.step++;
            updateLabels();
        }
    }
}