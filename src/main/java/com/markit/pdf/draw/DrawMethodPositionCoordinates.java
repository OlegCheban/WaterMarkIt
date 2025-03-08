package com.markit.pdf.draw;

import com.markit.api.positioning.PositionCoordinates;

/**
 * @author Oleg Cheban
 * @since 1.0
 */
public class DrawMethodPositionCoordinates extends PositionCoordinates {
    private final int MIN_X_EDGE_SIZE = 30;
    private final int MIN_Y_EDGE_SIZE = 100;

    public DrawMethodPositionCoordinates(int pageWidth, int pageHeight, int watermarkWidth, int watermarkHeight) {
        super(pageWidth, pageHeight, watermarkWidth, watermarkHeight);
    }

    @Override
    public Coordinates center() {
        return new Coordinates((getPageWidth() - getWatermarkWidth()) / 2, (getPageHeight() - getWatermarkHeight()) / 2 );
    }

    @Override
    public Coordinates topLeft() {
        return new Coordinates(MIN_X_EDGE_SIZE, MIN_Y_EDGE_SIZE);
    }

    @Override
    public Coordinates topRight() {
        return new Coordinates(getPageWidth() - getWatermarkWidth() - MIN_X_EDGE_SIZE, MIN_Y_EDGE_SIZE);
    }

    @Override
    public Coordinates topCenter() {
        return new Coordinates((getPageWidth() - getWatermarkWidth()) / 2, MIN_Y_EDGE_SIZE);
    }

    @Override
    public Coordinates bottomLeft() {
        return new Coordinates(MIN_X_EDGE_SIZE, getPageHeight() - getWatermarkHeight());
    }

    @Override
    public Coordinates bottomRight() {
        return new Coordinates(getPageWidth() - getWatermarkWidth() - MIN_X_EDGE_SIZE, getPageHeight() - getWatermarkHeight());
    }

    @Override
    public Coordinates bottomCenter() {
        return new Coordinates((getPageWidth() - getWatermarkWidth()) / 2, getPageHeight() - getWatermarkHeight());
    }

}
