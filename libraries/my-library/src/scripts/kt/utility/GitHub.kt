package scripts.kt.utility

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import org.tribot.script.sdk.Log
import scripts.kt.ScriptData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO


/* Written by IvanEOD 8/31/2022, at 11:31 AM */

object GitHub : Directory {

    override fun getDirectoryPath(): Path = createTRiBotPath("github")

    private const val GITHUB_URL = ScriptData.BaseGitHubUrl
    private const val GITHUB_TOKEN: String = ScriptData.GitHubToken
    private val authString: String? = if (GITHUB_TOKEN.isEmpty()) null else "Basic " + Base64.getEncoder()
        .encodeToString("$GITHUB_TOKEN:x-oauth-basic".toByteArray())

    private var _downloadCache: GithubDownloadCache? = null

    private var downloadCache: GithubDownloadCache
        get() {
            if (_downloadCache == null) {
                _downloadCache = load(GithubDownloadCache::class.java, "cache") ?: GithubDownloadCache()
            }
            return _downloadCache!!
        }
        set(value) {}


    @JvmStatic
    fun getImage(imageName: String): Image? {
        return this.loadGitImage(imageName)
    }

    @JvmStatic
    fun getImageFile(imageName: String): File {
        return getGitFile("images", "$imageName.png")
    }

    @JvmStatic
    fun getItemIconFile(itemId: Int): File {
        return getGitFile("images/items-icons", "$itemId.png")
    }

    @JvmStatic
    fun getFont(fontName: String): File {
        return getGitFile("fonts", "$fontName.ttf")
    }

    @JvmStatic
    fun getJson(jsonName: String): File {
        return getGitFile("json", "$jsonName.json")
    }

    @JvmStatic
    fun getFxml(fxmlName: String): File {
        return getGitFile("fxml", "$fxmlName.fxml")
    }

    @JvmStatic
    fun getCss(cssName: String): File {
        return getGitFile("css", "$cssName.css")
    }

    @JvmStatic
    private fun getGitFile(folder: String, fileName: String): File {
        val currentEtag = downloadCache[folder, fileName]
        val checkedTag = getETag(folder, fileName)
        val path = getPath(folder, fileName)
        if (currentEtag != checkedTag) {
            if (Files.exists(path) && path.toFile().exists()) Files.delete(path)
        }
        if (!Files.exists(path) || !path.toFile().exists()) {
            downloadCache[folder, fileName] = checkedTag
            downloadGitFile(path.toFile(), folder, fileName)
        }
        return path.toFile()
    }

    @JvmStatic
    fun getETag(folder: String, fileName: String): String {
        var etag = ""
        val urlString = "$GITHUB_URL$folder/${fileName.replace(" ", "%20")}"
        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.setRequestProperty("Authorization", authString)
            etag = connection.getHeaderField("ETag") ?: ""
        } catch (ioException: IOException) {
            Log.error("Error checking file from GitHub", ioException)
        }
        return etag.replace("\"".toRegex(), "")
    }



    @JvmStatic
    private fun loadGitImage(imageName: String): Image? {
        val file = getGitFile("images", imageName)
        return if (file.exists()) {
            try {
                SwingFXUtils.toFXImage(ImageIO.read(file), null)
            } catch (exception: IOException) {
                Log.error("Error loading image from GitHub", exception)
                null
            }
        } else null
    }

    @JvmStatic
    private fun downloadGitFile(file: File, folderName: String, fileName: String) {
        val urlString = "$GITHUB_URL$folderName/${fileName.replace(" ", "%20")}"
        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            if (!file.exists() && !Files.exists(file.toPath())) file.createNewFile()
            if (authString != null) connection.setRequestProperty("Authorization", authString)
            val etag = connection.getHeaderField("ETag")
            etag?.replace("\"".toRegex(), "")
            val inStream = connection.getInputStream()
            val output = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var index = 0
            while (-1 != inStream.read(buffer).also { index = it }) output.write(buffer, 0, index)
            inStream.close()
            output.close()
            downloadCache[folderName, fileName] = etag
            save(downloadCache, "cache")
        } catch (ioException: IOException) {
            Log.error("Error downloading file from GitHub", ioException)
        }
    }


    internal class GithubDownloadCache {
        private val cache = HashMap<String, HashMap<String, String>>()

        operator fun get(folder: String): HashMap<String, String> {
            return cache.getOrPut(folder) { HashMap() }
        }
        operator fun get(folder: String, fileName: String) = this[folder][fileName] ?: ""
        operator fun set(folder: String, fileName: String, etag: String) {
            this[folder][fileName] = etag
        }
        fun etagExists(folder: String, fileName: String) = this[folder].containsKey(fileName)


    }

}