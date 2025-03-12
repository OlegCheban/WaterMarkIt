package com.markit.image

import com.markit.api.ImageType
import com.markit.api.positioning.WatermarkPosition
import com.markit.api.WatermarkService
import com.markit.utils.TestFileUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImageWatermarkPositionTest {
    private lateinit var file: File

    @BeforeEach
    fun initDocument() {
        file = TestFileUtils.createJpegFile(TestFileUtils.outputDirectory + "test-position.jpeg")
    }

    @AfterEach
    fun closeDocument() {
        file.delete()
    }

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when watermark positioned at specific x and y then apply watermark`() {
        // When
        val result = WatermarkService.create()
            .watermarkImage(file, ImageType.JPEG)
            .withImage(TestFileUtils.readFileFromClasspathAsBytes("logo.png"))
                .size(50)
                .position(100, 200).end() // Setting specific X = 100, Y = 200
                .opacity(0.5f)
            .apply(TestFileUtils.outputDirectory, "image-position-test.jpeg")

        // Then
        assertNotNull(result, "The resulting file should not be null")
        assertTrue(Files.size(result) > file.length(), "The resulting file should be larger than the original")
    }
}
