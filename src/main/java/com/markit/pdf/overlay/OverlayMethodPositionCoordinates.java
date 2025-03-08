package com.markit.pdf.overlay;

import com.markit.api.positioning.PositionCoordinates;

/**
 * @author Oleg Cheban
 * @since 1.0
 */
public class OverlayMethodPositionCoordinates extends PositionCoordinates {
    private final int EDGE_SIZE = 10;

    public OverlayMethodPositionCoordinates(int pageWidth, int pageHeight, int watermarkWidth, int watermarkHeight) {
        super(pageWidth, pageHeight, watermarkWidth, watermarkHeight);
    }

    @Override
    public Coordinates center() {
        return new Coordinates((getPageWidth() - getWatermarkWidth()) / 2,(getPageHeight() - getWatermarkHeight()) / 2);
    }

    @Override
    public Coordinates topLeft() {
        return new Coordinates(EDGE_SIZE, getPageHeight() - getWatermarkHeight() - EDGE_SIZE);
    }

    @Override
    public Coordinates topRight() {
        return new Coordinates(getPageWidth() - getWatermarkWidth() - EDGE_SIZE,getPageHeight() - getWatermarkHeight() - EDGE_SIZE);
    }

    @Override
    public Coordinates topCenter() {
        return new Coordinates((getPageWidth() - getWatermarkWidth()) / 2, getPageHeight() - getWatermarkHeight() - EDGE_SIZE);
    }

    @Override
    public Coordinates bottomLeft() {
        return new Coordinates(EDGE_SIZE, getWatermarkHeight());
    }

    @Override
    public Coordinates bottomRight() {
        return new Coordinates(getPageWidth() - getWatermarkWidth() - EDGE_SIZE, getWatermarkHeight());
    }

    @Override
    public Coordinates bottomCenter() {
        return new Coordinates((getPageWidth() - getWatermarkWidth()) / 2, getWatermarkHeight());
    }
}
