package com.markit.image

import com.markit.utils.FileUtils
import com.markit.api.positioning.WatermarkPosition
import com.markit.api.WatermarkService
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WatermarkJpegText {

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when apply centered image-based watermark then make watermarked jpeg`() {
        val result = WatermarkService.create()
            .watermarkImage(FileUtils.readFileFromClasspathAsBytes("image.JPG"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                    .size(25)
                    .position(WatermarkPosition.CENTER).end()
                    .opacity(10)
            .apply()

        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(result.isNotEmpty(), "The resulting byte array should not be empty")
    }
}