package com.markit.video.ffmpeg.filters;

import com.markit.api.WatermarkAttributes;
import com.markit.api.positioning.Coordinates;
import com.markit.image.WatermarkPositioner;
import com.markit.video.ffmpeg.probes.VideoDimensions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * overlay filter chain builder with rotation support
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
        
        // Apply rotation if specified
        BufferedImage finalImage = scaledImage;
        Dimension finalDimensions = targetDimensions;
        int rotation = attr.getRotationDegrees();
        
        if (rotation != 0) {
            finalImage = rotateImage(scaledImage, rotation);
            finalDimensions = new Dimension(finalImage.getWidth(), finalImage.getHeight());
        }
        
        List<Coordinates> coordinates = calculateOverlayCoordinates(attr, dimensions, finalDimensions);

        return new OverlayContext(finalImage, coordinates, tempImages, lastLabel, step, isEmptyFilter);
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

    /**
     * Rotates an image by the specified angle in degrees.
     * Uses the same rotation logic as text-based watermarks for consistency.
     * 
     * @param image The image to rotate
     * @param degrees The rotation angle in degrees (positive = clockwise)
     * @return A new BufferedImage with the rotated content
     */
    private BufferedImage rotateImage(BufferedImage image, int degrees) {
        // Convert degrees to radians
        double radians = Math.toRadians(degrees);
        
        // Calculate new dimensions after rotation
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        
        int newWidth = (int) Math.floor(originalWidth * cos + originalHeight * sin);
        int newHeight = (int) Math.floor(originalHeight * cos + originalWidth * sin);
        
        // Create new image with calculated dimensions
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        
        try {
            configureGraphics(g2d);
            
            // Rotate around the center of the new image
            AffineTransform transform = new AffineTransform();
            transform.translate(newWidth / 2.0, newHeight / 2.0);
            transform.rotate(radians);
            transform.translate(-originalWidth / 2.0, -originalHeight / 2.0);
            
            g2d.setTransform(transform);
            g2d.drawImage(image, 0, 0, null);
        } finally {
            g2d.dispose();
        }
        
        return rotated;
    }

    private BufferedImage createScaledImage(Dimension size) {
        return new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    }

    private void configureGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        private String lastLabel;
        private int step;
        private boolean isEmptyFilter;
        private String inLabel;
        private String outLabel;

        OverlayContext(BufferedImage scaledImage, List<Coordinates> coordinates, List<File> tempImages,
                       String lastLabel, int step, boolean isEmptyFilter) {
            this.scaledImage = scaledImage;
            this.coordinates = coordinates;
            this.tempImages = tempImages;
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