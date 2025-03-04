package com.markit.image

import com.markit.api.ImageType
import com.markit.api.positioning.WatermarkPosition
import com.markit.api.WatermarkService
import com.markit.utils.TestFileUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImageBasedWatermarkImageBasedWatermarkTest {
    private lateinit var file: File;

    @BeforeEach
    fun initDocument(){
        file = TestFileUtils.createJpegFile(TestFileUtils.outputDirectory + "test.jpeg")
    }

    @AfterEach
    fun closeDocument() {
        file.delete();
    }

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then make image-based watermarked jpeg using apply(String directoryPath, String fileName)`() {
        // When
        val result = WatermarkService.create()
            .watermarkImage(file, ImageType.JPEG)
                .withImage(TestFileUtils.readFileFromClasspathAsBytes("logo.png"))
                    .size(25)
                    .position(WatermarkPosition.TILED).end()
                    .opacity(10)
            .apply(TestFileUtils.outputDirectory, "image-test.jpeg")

        // Then
        assertNotNull(result, "The resulting byte array should not be null")
        assertTrue(Files.size(result) > file.length(), "The resulting byte array should be bigger than initial one")
    }

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then directory does not exist throw IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            WatermarkService.create()
                .watermarkImage(file, ImageType.JPEG)
                .withImage(TestFileUtils.readFileFromClasspathAsBytes("logo.png")).size(25)
                .position(WatermarkPosition.TILED).end()
                .opacity(10)
                .apply("./doesnt-exist/", "image-test.jpeg")
        }
    }

    @Test
    @Throws(IOException::class)
    fun `given jpeg file when draw method then it is not a directory throw IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            WatermarkService.create()
                .watermarkImage(file, ImageType.JPEG)
                .withImage(TestFileUtils.readFileFromClasspathAsBytes("logo.png")).size(25)
                .position(WatermarkPosition.TILED).end()
                .opacity(10)
                .apply("not a directory", "image-test.jpeg")
        }
    }
}