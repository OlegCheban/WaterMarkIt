package com.markit.api

import org.apache.pdfbox.pdmodel.PDDocument
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*
import java.util.function.Predicate

data class WatermarkAttributes (
    var text: String = "",
    var size: Int = 100,
    var color: Color = Color.BLACK,
    var opacity: Float = 0.4f,
    var dpi: Float = 300f,
    var trademark: Boolean = false,
    var rotation: Int = 0,
    var method: WatermarkingMethod = WatermarkingMethod.DRAW,
    var position: WatermarkPosition = WatermarkPosition.CENTER,
    var adjustment: WatermarkAdjustment = WatermarkAdjustment(),
    var image: Optional<BufferedImage> = Optional.empty(),
    var documentPredicates: Predicate<PDDocument> = Predicate { true },
    var pagePredicate: Predicate<Int> = Predicate { true },
    var watermarkEnabled: Boolean = true
) {
}

