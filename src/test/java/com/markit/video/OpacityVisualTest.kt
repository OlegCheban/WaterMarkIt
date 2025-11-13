package com.markit.video

import com.markit.utils.FileUtils
import com.markit.api.positioning.WatermarkPosition
import com.markit.api.WatermarkService
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class OpacityVisualTest {
    
    @Test
    @Throws(IOException::class)
    fun `generate videos with different opacity levels for visual comparison`() {
        // Test 1: 25% opacity (very transparent)
        val result25 = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.CENTER).end()
                .size(40)
                .opacity(25)
            .apply()
        
        val output25 = File("opacity-25-percent.mp4")
        output25.writeBytes(result25)
        println("✓ Created: opacity-25-percent.mp4 (very transparent)")
        
        // Test 2: 50% opacity (medium transparency)
        val result50 = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.CENTER).end()
                .size(40)
                .opacity(50)
            .apply()
        
        val output50 = File("opacity-50-percent.mp4")
        output50.writeBytes(result50)
        println("✓ Created: opacity-50-percent.mp4 (medium transparency)")
        
        // Test 3: 75% opacity (mostly opaque)
        val result75 = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.CENTER).end()
                .size(40)
                .opacity(75)
            .apply()
        
        val output75 = File("opacity-75-percent.mp4")
        output75.writeBytes(result75)
        println("✓ Created: opacity-75-percent.mp4 (mostly opaque)")
        
        // Test 4: 100% opacity (fully opaque - for comparison)
        val result100 = WatermarkService.create()
            .watermarkVideo(FileUtils.readFileFromClasspathAsBytes("video.mp4"))
                .withImage(FileUtils.readFileFromClasspathAsBytes("logo.png"))
                .position(WatermarkPosition.CENTER).end()
                .size(40)
                .opacity(100)
            .apply()
        
        val output100 = File("opacity-100-percent.mp4")
        output100.writeBytes(result100)
        println("✓ Created: opacity-100-percent.mp4 (fully opaque)")
        
        // Assertions
        assertNotNull(result25)
        assertNotNull(result50)
        assertNotNull(result75)
        assertNotNull(result100)
        
        assertTrue(result25.isNotEmpty())
        assertTrue(result50.isNotEmpty())
        assertTrue(result75.isNotEmpty())
        assertTrue(result100.isNotEmpty())
        
        println("\n✅ All 4 videos created successfully!")
        println("📂 Check your project root for:")
        println("   - opacity-25-percent.mp4")
        println("   - opacity-50-percent.mp4")
        println("   - opacity-75-percent.mp4")
        println("   - opacity-100-percent.mp4")
        println("\n🎬 Open them and compare the transparency levels!")
    }
}