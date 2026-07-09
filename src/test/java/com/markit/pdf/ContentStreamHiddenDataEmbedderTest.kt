package com.markit.pdf

import com.markit.pdf.hidden.ContentStreamHiddenDataEmbedder
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ContentStreamHiddenDataEmbedderTest {

    private lateinit var embedder: ContentStreamHiddenDataEmbedder

    @BeforeEach
    fun setUp() {
        embedder = ContentStreamHiddenDataEmbedder()
    }

    @Test
    fun `should embed and extract hidden string data successfully`() {
        val original = "user-id-99887766"
        PDDocument().use { doc ->
            doc.addPage(PDPage())
            embedder.embed(doc, original.toByteArray())
            val extracted = embedder.extract(doc)
            assertNotNull(extracted)
            assertEquals(original, String(extracted!!))
        }
    }

    @Test
    fun `should embed and extract binary data without corruption`() {
        val binaryData = byteArrayOf(0x00, 0x01, 0x7F, 0xFF.toByte(), 0xFE.toByte())
        PDDocument().use { doc ->
            doc.addPage(PDPage())
            embedder.embed(doc, binaryData)
            val extracted = embedder.extract(doc)
            assertArrayEquals(binaryData, extracted)
        }
    }

    @Test
    fun `should return null when no hidden data is present`() {
        PDDocument().use { doc ->
            doc.addPage(PDPage())
            val result = embedder.extract(doc)
            assertNull(result)
        }
    }

    @Test
    fun `should preserve existing page content after embedding`() {
        val hiddenData = "trace-abc-123"
        PDDocument().use { doc ->
            doc.addPage(PDPage())
            embedder.embed(doc, hiddenData.toByteArray())
            val extracted = embedder.extract(doc)

            assertNotNull(extracted)
            assertEquals(hiddenData, String(extracted!!))
        }
    }

    @Test
    fun `should survive save and reload round-trip`() {
        val original = "round-trip-test-data"
        val outputStream = java.io.ByteArrayOutputStream()

        PDDocument().use { doc ->
            doc.addPage(PDPage())
            embedder.embed(doc, original.toByteArray())
            doc.save(outputStream)
        }

        PDDocument.load(outputStream.toByteArray()).use { reloaded ->
            val extracted = embedder.extract(reloaded)
            assertNotNull(extracted)
            assertEquals(original, String(extracted!!))
        }
    }

    @Test
    fun `should throw IllegalArgumentException when document is null`() {
        assertThrows<IllegalArgumentException> {
            embedder.embed(null, "data".toByteArray())
        }
    }

    @Test
    fun `should throw IllegalArgumentException when data is empty`() {
        PDDocument().use { doc ->
            doc.addPage(PDPage())
            assertThrows<IllegalArgumentException> {
                embedder.embed(doc, byteArrayOf())
            }
        }
    }

    @Test
    fun `should not affect PDF rendering by verifying page count is unchanged`() {
        val data = "invisible-watermark"
        PDDocument().use { doc ->
            doc.addPage(PDPage())
            doc.addPage(PDPage())
            val pageCountBefore = doc.numberOfPages
            embedder.embed(doc, data.toByteArray())
            assertEquals(pageCountBefore, doc.numberOfPages)
        }
    }
}