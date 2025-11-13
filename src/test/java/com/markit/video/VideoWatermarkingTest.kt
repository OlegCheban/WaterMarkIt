package com.markit.video

import com.markit.utils.FileUtils
import com.markit.api.positioning.WatermarkPosition
import com.markit.api.WatermarkService
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.IOException
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class VideoWatermarkingTest {
    
    @Test
    @Throws(IOException::class)
    fun `given video when apply several watermarks then make watermarked vidio`() {
        val result = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withText("WaterMarkIt").color(Color.RED).end()
                .opacity(50)
                .position(WatermarkPosition.CENTER).end()
                .size(30)
            .and()
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.BOTTOM_RIGHT).end()
                .size(8)
                .opacity(40)  // NEW: Image opacity feature
            .and()
                .withText("WaterMarkIt").end()
                .position(WatermarkPosition.BOTTOM_LEFT).end()
                .size(40)
            .and()
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.TOP_LEFT).end()
                .size(8)
            .apply()

        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(result.isNotEmpty(), "The resulting byte array should not be empty")
        
        // Save output for verification
        val outputFile = File("my-watermarked-video.mp4")
        outputFile.writeBytes(result)
    }
}