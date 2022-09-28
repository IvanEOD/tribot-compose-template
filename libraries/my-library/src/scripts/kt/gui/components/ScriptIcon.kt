package scripts.kt.gui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.skia.Image
import scripts.kt.gui.toComposeImageBitmap
import scripts.kt.utility.AssetManager
import java.awt.image.BufferedImage


/* Written by IvanEOD 9/28/2022, at 9:21 AM */

// There are so many ways to make an icon, this wraps some into one universal class

interface ScriptIcon {

    val painter: @Composable () -> Painter

    companion object {
        private fun new(painter: @Composable () -> Painter) = object : ScriptIcon {
            override val painter: @Composable () -> Painter = painter
        }
        @JvmStatic
        @get:JvmName("TRiBotLogo")
        val TRiBotLogo = new { AssetManager.loadGithubImageToPainter("Tribot-Logo") }
        @JvmStatic
        fun fromImageVector(imageVector: ImageVector) = new { rememberVectorPainter(imageVector) }
        @JvmStatic
        fun fromImageBitmap(imageBitmap: ImageBitmap) = new { BitmapPainter(imageBitmap) }
        @JvmStatic
        fun fromPainter(painter: Painter) = new { painter }
        @JvmStatic
        fun fromImage(image: Image) = new { BitmapPainter(image.toComposeImageBitmap()) }
        @JvmStatic
        fun fromImage(image: java.awt.Image) = new { BitmapPainter(image.toComposeImageBitmap()) }
        @JvmStatic
        fun fromBufferedImage(image: BufferedImage) = new { BitmapPainter(image.toComposeImageBitmap()) }
        @JvmStatic
        fun fromGithubImage(imageName: String) = new { AssetManager.loadGithubImageToPainter(imageName) }
        @JvmStatic
        fun fromUrl(url: String) = new { AssetManager.loadUrlImageToPainter(url) }
        @JvmStatic
        val ImageNotFound = new { AssetManager.loadGithubImageToPainter("image-not-found") }
        @JvmStatic
        val None = new { BitmapPainter(ImageBitmap(0, 0)) }

    }


}