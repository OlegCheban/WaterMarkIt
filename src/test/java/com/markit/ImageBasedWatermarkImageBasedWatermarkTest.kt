package com.markit

import com.markit.api.FileType
import com.markit.api.WatermarkPosition
import com.markit.api.WatermarkService
import com.markit.exceptions.WatermarkingException
import com.markit.utils.TestFileUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImageBasedWatermarkImageBasedWatermarkTest {
    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then make image-based watermarked jpeg using apply(String directoryPath, String fileName)`() {
        // Given
        val file = TestFileUtils.createJpegFile(TestFileUtils.outputDirectory + "test.jpeg")

        // When
        val result = WatermarkService.imageBasedWatermarker()
            .watermark(file, FileType.JPEG)
            .withImage(TestFileUtils.readFileFromClasspathAsBytes("logo.png")).size(25)
            .position(WatermarkPosition.TILED)
            .opacity(0.1f)
            .apply(TestFileUtils.outputDirectory, "image-test.jpeg")

        // Then
        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(Files.size(result) > 0, "The resulting byte array should not be empty")
        assertTrue(Files.size(result) > file.length(), "The resulting byte array should be bigger than initial one")
        file.delete();
    }

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then directory does not exist throw IllegalArgumentException`() {
        // Given
        val file = TestFileUtils.createJpegFile(TestFileUtils.outputDirectory + "test.jpeg")

        // When & Then
        assertThrows<IllegalArgumentException> {
            WatermarkService.imageBasedWatermarker()
                .watermark(file, FileType.JPEG)
                .withImage(TestFileUtils.readFileFromClasspathAsBytes("logo.png")).size(25)
                .position(WatermarkPosition.TILED)
                .opacity(0.1f)
                .apply("./doesnt-exist/", "image-test.jpeg")
        }
        file.delete()
    }

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then it is not a directory throw IllegalArgumentException`() {
        // Given
        val file = TestFileUtils.createJpegFile(TestFileUtils.outputDirectory + "test.jpeg")

        // When & Then
        assertThrows<IllegalArgumentException> {
            WatermarkService.imageBasedWatermarker()
                .watermark(file, FileType.JPEG)
                .withImage(TestFileUtils.readFileFromClasspathAsBytes("logo.png")).size(25)
                .position(WatermarkPosition.TILED)
                .opacity(0.1f)
                .apply("not a directory", "image-test.jpeg")
        }
        file.delete()
    }

    @Test
    @Throws(IOException::class)
    fun `given invalid file then throw WatermarkingException`() {
        // Given
        val invalidFile = File("invalid-file-path")

        // When & Then
        assertThrows<WatermarkingException> {
            WatermarkService.imageBasedWatermarker()
                .watermark(invalidFile, FileType.JPEG)
                .withImage(TestFileUtils.readFileFromClasspathAsBytes("logo.png")).size(25)
                .position(WatermarkPosition.TILED)
                .opacity(0.1f)
                .apply(TestFileUtils.outputDirectory, "image-test.jpeg")
        }
        invalidFile.delete()
    }
}