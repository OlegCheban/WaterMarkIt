package com.markit.utils

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import javax.imageio.ImageIO

object TestFileUtils {
    @JvmStatic
    var outputDirectory: String = "src/test/java/com/markit/output/"

    @JvmStatic
    fun createJpegFile(fileName: String, width: Int = 600, height: Int = 800): File {
        require(width > 0) { "Width must be greater than 0" }
        require(height > 0) { "Height must be greater than 0" }
        try{
            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val g2d = image.createGraphics()
            g2d.color = Color.WHITE
            g2d.fillRect(0, 0, width, height)
            g2d.dispose()
            val file = File(fileName)
            ImageIO.write(image, "jpeg", file)
            return file
        } catch (e: Exception) {
            throw RuntimeException("Failed to create JPEG file: ${e.message}", e)
        }
    }
    @JvmStatic
    fun readFileFromClasspathAsBytes(fileName: String): ByteArray? {
        val classLoader = Thread.currentThread().contextClassLoader
        val inputStream: InputStream? = classLoader.getResourceAsStream(fileName)
        return inputStream?.readBytes()
    }

    @JvmStatic
    fun outputFile(result: ByteArray, filename: String) {
        val outputFile = File(filename)
        Files.write(outputFile.toPath(), result)
    }
}