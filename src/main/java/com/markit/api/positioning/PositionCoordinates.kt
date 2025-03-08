package com.markit.api.positioning

import com.markit.api.WatermarkAttributes

/**
 * @author Oleg Cheban
 * @since 1.0
 */
abstract class PositionCoordinates(
    protected val pageWidth: Int,
    protected val pageHeight: Int,
    protected val watermarkWidth: Int,
    protected val watermarkHeight: Int
) : WatermarkPositionCoordinates {

    fun getCoordinatesForAttributes(attr: WatermarkAttributes): List<WatermarkPositionCoordinates.Coordinates> {
        var coordinates = when (attr.position) {
            WatermarkPosition.CENTER -> listOf(center())
            WatermarkPosition.TOP_LEFT -> listOf(topLeft())
            WatermarkPosition.TOP_RIGHT -> listOf(topRight())
            WatermarkPosition.BOTTOM_LEFT -> listOf(bottomLeft())
            WatermarkPosition.BOTTOM_RIGHT -> listOf(bottomRight())
            WatermarkPosition.TILED -> tiled(attr)
            WatermarkPosition.CUSTOM -> listOf(attr.positionAdjustment)
            else -> throw IllegalArgumentException("Unsupported watermark position: ${attr.position}")
        }
        if (attr.positionAdjustment.x != 0 || attr.positionAdjustment.y != 0) {
            coordinates = coordinates.map {
                WatermarkPositionCoordinates.Coordinates(
                    it.x + attr.positionAdjustment.x,
                    it.y + attr.positionAdjustment.y
                )
            }
        }
        return coordinates
    }

    override fun tiled(attr: WatermarkAttributes): List<WatermarkPositionCoordinates.Coordinates> {
        val numHorizontal = (pageWidth + watermarkWidth - 1) / watermarkWidth
        val numVertical = (pageHeight + watermarkHeight - 1) / watermarkHeight
        return (0 until numHorizontal).flatMap { i ->
            (0 until numVertical).map { j ->
                WatermarkPositionCoordinates.Coordinates(
                    (i * watermarkWidth) + (i * attr.horizontalSpacing),
                    (j * watermarkHeight) + (j * attr.verticalSpacing)
                )
            }
        }
    }
}
