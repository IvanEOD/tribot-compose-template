package scripts.kt.utility

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.unit.Density
import org.xml.sax.InputSource
import java.io.File
import java.net.URL
import java.nio.file.Path


/* Written by IvanEOD 9/17/2022, at 5:02 PM */
object AssetManager : Directory {

    init {
        ImageCache.preload("image-not-found") { loadGithubImageToBitmap("image-not-found") }
    }

    override fun getDirectoryPath(): Path = createTRiBotPath("assets")
    private fun getWikiUrl(name: String) = "https://oldschool.runescape.wiki/w/File:${name.replace(" ", "_")}.png"
    private fun getImageFile(name: String) : File {
        val file = getFile("images", "${name.replace(" ", "_")}.png")
        if (!file.exists()) {
            val url = URL(getWikiUrl(name))
            url.openStream().use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        return file
    }

    fun loadUrlImageToBitmap(url: String) : ImageBitmap = loadImageBitmap(url)
    fun loadUrlImageToPainter(url: String) : Painter = loadImagePainter(url)

    fun loadWikiImageToBitmap(name: String) : ImageBitmap = loadImageBitmap(getImageFile(name))
    fun loadWikiImageToPainter(name: String) : Painter = loadSvgPainter(getImageFile(name), Density(1f))

    fun loadGithubItemIconToBitmap(id: Int) : ImageBitmap = loadImageBitmap(GitHub.getItemIconFile(id))
    fun loadGithubItemIconToPainter(id: Int) : Painter = loadSvgPainter(GitHub.getItemIconFile(id), Density(1f))

    fun loadGithubImageToBitmap(name: String) : ImageBitmap = loadImageBitmap(GitHub.getImageFile(name))
    fun loadGithubImageToBitmap(name: String, default: String) : ImageBitmap = loadImageBitmap(GitHub.getImageFile(name), GitHub.getImageFile(default))
    fun loadGithubImageToPainter(name: String) : Painter = BitmapPainter(loadImageBitmap(GitHub.getImageFile(name)))
    fun loadGithubImageToPainter(name: String, default: String) : Painter = BitmapPainter(loadImageBitmap(GitHub.getImageFile(name)))

    fun loadImageNotFoundBitmap(): ImageBitmap = loadGithubImageToBitmap("image-not-found")
    fun loadImageNotFoundPainter(): Painter = loadGithubImageToPainter("image-not-found")

    private fun loadImageBitmap(file: File): ImageBitmap = if (file.exists()) file.inputStream().buffered().use { return loadImageBitmap(it) }
        else loadGithubImageToBitmap("image-not-found")
    private fun loadImageBitmap(file: File, default: File): ImageBitmap = if (file.exists()) file.inputStream().buffered().use { return loadImageBitmap(it) }
        else if (default.exists()) default.inputStream().buffered().use { return loadImageBitmap(it) }
        else loadGithubImageToBitmap("image-not-found")
    private fun loadSvgPainter(file: File, density: Density): Painter = if (file.exists()) file.inputStream().buffered().use { return loadSvgPainter(it, density) }
        else loadGithubImageToPainter("image-not-found")
    private fun loadSvgPainter(file: File, density: Density, default: File): Painter = if (file.exists()) file.inputStream().buffered().use { return loadSvgPainter(it, density) }
        else if (default.exists()) default.inputStream().buffered().use { return loadSvgPainter(it, density) }
        else loadGithubImageToPainter("image-not-found")

    private fun loadImageBitmap(url: String): ImageBitmap = URL(url).openStream().buffered().use { return loadImageBitmap(it) }
    private fun loadImagePainter(url: String): Painter = URL(url).openStream().buffered().use { return BitmapPainter(loadImageBitmap(it)) }
    private fun loadSvgPainter(url: String, density: Density): Painter = URL(url).openStream().buffered().use { return loadSvgPainter(it, density) }
    private fun loadXmlImageVector(url: String, density: Density): ImageVector = URL(url).openStream().buffered().use { return loadXmlImageVector(InputSource(it), density) }


}