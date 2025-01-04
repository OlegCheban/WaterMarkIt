package com.markit

import com.markit.api.FileType
import com.markit.api.WatermarkPosition
import com.markit.api.WatermarkService
import com.markit.exceptions.WatermarkingException
import com.markit.utils.TestFileUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SuppressWarnings("deprecation")
class TextBasedWatermarkTextBasedWatermarkTest {
    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then make watermarked jpeg`() {
        // Given
        val file = createJpegFile("test.jpeg");

        // When
        val result = WatermarkService.textBasedWatermarker()
                .watermark(file, FileType.JPEG)
                .withText("Sample Watermark")
                .size(30)
                .position(WatermarkPosition.CENTER)
                .adjust(5, 5)
                .apply()

        // Then
        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(result.isNotEmpty(), "The resulting byte array should not be empty")
        assertTrue(result.size > file.length(), "The resulting byte array should be bigger than initial one")
        //outputFile(result, "test.jpeg")
        file.delete();
    }

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then make watermarked jpeg using apply(String directoryPath, String fileName)`() {
        // Given
//        val file = createJpegFile("test.jpeg")
        val file = TestFileUtils.createJpegFile(TestFileUtils.outputDirectory + "test.jpeg")

        // When
        val result = WatermarkService.textBasedWatermarker()
            .watermark(file, FileType.JPEG)
            .withText("Sample Watermark")
            .size(30)
            .position(WatermarkPosition.CENTER)
            .adjust(5, 5)
            .apply(TestFileUtils.outputDirectory, "text-test.jpeg")

        // Then
        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(Files.size(result) > 0, "The resulting byte array should not be empty")
        assertTrue(Files.size(result) > file.length(), "The resulting byte array should be bigger than initial one")
        file.delete()
    }

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then directory does not exist throw IllegalArgumentException`() {
        // Given
        val file = TestFileUtils.createJpegFile(TestFileUtils.outputDirectory + "test.jpeg")

        // When & Then
        assertThrows<IllegalArgumentException> {
            WatermarkService.textBasedWatermarker()
                .watermark(file, FileType.JPEG)
                .withText("Sample Watermark")
                .size(30)
                .position(WatermarkPosition.CENTER)
                .adjust(5, 5)
                .apply("./doesnt-exist/", "text-test.jpeg")
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
            WatermarkService.textBasedWatermarker()
                .watermark(file, FileType.JPEG)
                .withText("Sample Watermark")
                .size(30)
                .position(WatermarkPosition.CENTER)
                .adjust(5, 5)
                .apply("not a directory", "text-test.jpeg")
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
            WatermarkService.textBasedWatermarker()
                .watermark(invalidFile, FileType.JPEG)
                .withText("Sample Watermark")
                .size(30)
                .position(WatermarkPosition.CENTER)
                .adjust(5, 5)
                .apply(TestFileUtils.outputDirectory, "text-test.jpeg")
        }
        invalidFile.delete()
    }

    fun createJpegFile(fileName: String, width: Int = 600, height: Int = 800): File {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g2d = image.createGraphics()
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, width, height)
        g2d.dispose()
        val file = File(fileName)
        ImageIO.write(image, "jpg", file)
        return file;
    }

    private fun outputFile(result: ByteArray, filename: String) {
        val outputFile = File(filename)
        Files.write(outputFile.toPath(), result)
    }
}
