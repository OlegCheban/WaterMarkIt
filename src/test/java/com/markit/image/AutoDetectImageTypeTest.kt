package com.markit.image

import com.markit.api.WatermarkService
import org.junit.jupiter.api.Test
import java.awt.Color
import java.io.File
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AutoDetectImageTypeTest {

    @Test
    fun `given PNG image when Apply Watermark without specifying type then auto-detect`() {
        val file = File(javaClass.classLoader.getResource("logo.png")!!.toURI())

        val result = WatermarkService.create()
            .watermarkImage(file) // 👈 no ImageType passed
            .withText("Auto-detect Test")
            .color(Color.BLUE).end()
            .opacity(40)
            .size(25)
            .apply()

        assertNotNull(result, "Result should not be null")
        assertTrue(result.isNotEmpty(), "Result should not be empty")
    }

    @Test
    fun `given non-image file when auto-detect then throw error`() {
        val file = File.createTempFile("test", ".txt")
        file.writeText("Not an image!")

        assertFailsWith<UnsupportedOperationException> {
            WatermarkService.create().watermarkImage(file)
        }
    }
}
