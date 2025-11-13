package com.markit.video

import com.markit.utils.FileUtils
import com.markit.api.positioning.WatermarkPosition
import com.markit.api.WatermarkService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import java.io.IOException
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Unit tests for image watermark opacity feature (Issue #105)
 * Tests various opacity levels and edge cases
 */
class ImageOpacityUnitTest {

    @Test
    @DisplayName("Test image watermark with 50% opacity produces valid output")
    @Throws(IOException::class)
    fun `test 50 percent opacity watermark`() {
        val result = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.CENTER).end()
                .size(30)
                .opacity(50)
            .apply()

        assertNotNull(result, "Result should not be null")
        assertTrue(result.isNotEmpty(), "Result should contain video data")
        assertTrue(result.size > 1000, "Video file should be substantial size")
    }

    @Test
    @DisplayName("Test image watermark with 25% opacity (very transparent)")
    @Throws(IOException::class)
    fun `test 25 percent opacity watermark`() {
        val result = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.TOP_LEFT).end()
                .size(20)
                .opacity(25)
            .apply()

        assertNotNull(result, "Result should not be null")
        assertTrue(result.isNotEmpty(), "Result should contain video data")
    }

    @Test
    @DisplayName("Test image watermark with 75% opacity (mostly opaque)")
    @Throws(IOException::class)
    fun `test 75 percent opacity watermark`() {
        val result = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.BOTTOM_RIGHT).end()
                .size(15)
                .opacity(75)
            .apply()

        assertNotNull(result, "Result should not be null")
        assertTrue(result.isNotEmpty(), "Result should contain video data")
    }

    @Test
    @DisplayName("Test backward compatibility - image watermark without opacity parameter")
    @Throws(IOException::class)
    fun `test backward compatibility no opacity specified`() {
        // This tests that existing code without .opacity() still works
        val result = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.CENTER).end()
                .size(20)
                // No .opacity() call - should default to 100% (fully opaque)
            .apply()

        assertNotNull(result, "Result should not be null")
        assertTrue(result.isNotEmpty(), "Result should contain video data")
    }

    @Test
    @DisplayName("Test edge case - 100% opacity (should behave same as no opacity)")
    @Throws(IOException::class)
    fun `test 100 percent opacity edge case`() {
        val result = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.TOP_RIGHT).end()
                .size(15)
                .opacity(100)
            .apply()

        assertNotNull(result, "Result should not be null")
        assertTrue(result.isNotEmpty(), "Result should contain video data")
    }

    @Test
    @DisplayName("Test multiple image watermarks with different opacities")
    @Throws(IOException::class)
    fun `test multiple watermarks with varying opacity levels`() {
        val result = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.TOP_LEFT).end()
                .size(15)
                .opacity(30)
            .and()
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.TOP_RIGHT).end()
                .size(15)
                .opacity(60)
            .and()
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.BOTTOM_LEFT).end()
                .size(15)
                .opacity(90)
            .apply()

        assertNotNull(result, "Result should not be null")
        assertTrue(result.isNotEmpty(), "Result should contain video data")
        assertTrue(result.size > 1000, "Video with multiple watermarks should be substantial")
    }

    @Test
    @DisplayName("Test edge case - very low opacity (10%)")
    @Throws(IOException::class)
    fun `test very low opacity watermark`() {
        val result = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.CENTER).end()
                .size(40)
                .opacity(10)
            .apply()

        assertNotNull(result, "Result should not be null")
        assertTrue(result.isNotEmpty(), "Result should contain video data")
    }
}