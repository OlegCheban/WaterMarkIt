package com.markit.pdf

import com.markit.api.WatermarkPosition
import com.markit.api.WatermarkService
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImageWatermarkTest : BasePdfWatermarkTest() {
    @BeforeEach
    override fun initDocument() {
        document = PDDocument().apply {
            addPage(PDPage(PDRectangle.A4))
        }
    }

    @Test
    @Throws(IOException::class)
    fun `given Pdf when Image Watermark with 180 Degree Rotation then Apply Watermark`() {
        // When
        val result = WatermarkService.create()
            .watermarkPDF(document)
                .withImage(readFileFromClasspathAsBytes("logo.png"))
                    .size(25)
                    .rotation(180)
                    .position(WatermarkPosition.TILED)
            .apply()

        // Then
        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(result.isNotEmpty(), "The resulting byte array should not be empty")
        //outputFile(result, "ImageBasedWatermarkRotation.pdf")
    }

    @Test
    @Throws(IOException::class)
    fun `given Pdf when Image Watermark and DPI then Apply Watermark`() {
        // When
        val result = WatermarkService.create()
            .watermarkPDF(document)
                .withImage(readFileFromClasspathAsBytes("logo.png"))
                    .size(15)
                    .dpi(100)
                    .position(WatermarkPosition.CENTER)
            .apply()

        // Then
        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(result.isNotEmpty(), "The resulting byte array should not be empty")
        //outputFile(result, "ImageBasedWatermarkDPI.pdf")
    }

    @Test
    @Throws(IOException::class)
    fun `given Pdf when Image Watermark and Adjust then Apply Watermark`() {
        // When
        val result = WatermarkService.create()
            .watermarkPDF(document)
                .withImage(readFileFromClasspathAsBytes("logo.png"))
                    .size(25)
                    .position(com.markit.api.WatermarkPosition.TILED)
                        .adjust(50, 50)
            .apply()

        // Then
        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(result.isNotEmpty(), "The resulting byte array should not be empty")
        //outputFile(result, "ImageBasedWatermarkAdjust.pdf")
    }
}