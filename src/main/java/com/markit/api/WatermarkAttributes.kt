package com.markit.api

import com.markit.api.positioning.WatermarkPosition
import com.markit.api.positioning.WatermarkPositionCoordinates
import org.apache.pdfbox.pdmodel.PDDocument
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*
import java.util.function.Predicate

/**
 * @author Oleg Cheban
 * @since 1.0
 */
data class WatermarkAttributes (
    var text: String = "",
    var size: Int = 100,
    var color: Color = Color.BLACK,
    var opacity: Float = 0.4f,
    var dpi: Float = 300f,
    var trademark: Boolean = false,
    var rotationDegrees: Int = 0,
    var method: WatermarkingMethod = WatermarkingMethod.DRAW,
    var position: WatermarkPosition = WatermarkPosition.CENTER,
    var positionAdjustment: WatermarkPositionCoordinates.Coordinates = WatermarkPositionCoordinates.Coordinates(0, 0),
    var verticalSpacing: Int = 50,
    var horizontalSpacing: Int = 50,
    var image: Optional<BufferedImage> = Optional.empty(),
    var documentPredicate: Predicate<PDDocument> = Predicate { true },
    var pagePredicate: Predicate<Int> = Predicate { true },
    var watermarkEnabled: Boolean = true
){
    fun setPosition(x: Int, y: Int) {
        this.position = WatermarkPosition.CUSTOM // Ensure CUSTOM exists
        this.positionAdjustment = WatermarkPositionCoordinates.Coordinates(x, y)
    }
}