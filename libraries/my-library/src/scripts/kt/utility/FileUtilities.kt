package scripts.kt.utility

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.AgeFileFilter
import org.tribot.script.sdk.Log
import org.tribot.script.sdk.Tribot
import scripts.kt.ScriptData
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.imageio.ImageIO


/* Written by IvanEOD 8/31/2022, at 11:01 AM */

private const val ROOT_DIRECTORY = ScriptData.SaveDirectoryName
private const val FILE_REGEX = "[^a-zA-Z\\d\\-_.]+"
private val GSON = GsonBuilder().registerTypeAdapterFactory(StateTypAdapterFactory()).create()

fun createDirectories(path: Path): Path {
    return if (path.toFile().exists()) path else try {
        Files.createDirectories(path)
    } catch (e: IOException) {
        throw RuntimeException("Failed to create directory for path: $path", e)
    }
}

fun createSubDirectories(path: Path) {
    val parentDirectory = path.parent
    createDirectories(parentDirectory)
}

fun createTRiBotPath(vararg chunks: String): Path {
    val rootPath: Path = Tribot.getDirectory().toPath().resolve(ROOT_DIRECTORY)
    return createDirectories(Paths.get(rootPath.toString(), *formatFileName(*chunks)))
}

fun deleteFilesOlderThan(duration: Long, unit: TimeUnit, path: Path, recursive: Boolean): Boolean {
    val directoryFile = path.toFile()
    val filter = AgeFileFilter(TimeUnit.MILLISECONDS.convert(duration, unit))
    val files = directoryFile.listFiles(filter as FileFilter) ?: return true
    for (file in files) {
        if (file.isDirectory) {
            if (recursive) deleteFilesOlderThan(duration, unit, file.toPath(), true)
            val childFiles = file.listFiles()
            if (childFiles == null || childFiles.isEmpty()) file.delete()
        } else {
            if (file.delete()) {
                Log.trace("Deleted file: ${file.name}")
            }
        }
    }
    return true
}

fun deleteFiles(predicate: (File) -> Boolean, path: Path, recursive: Boolean): Boolean {
    val directoryFile = path.toFile()
    val files = directoryFile.listFiles() ?: return true
    for (file in files) {
        if (file.isDirectory) {
            if (recursive) deleteFiles(predicate, file.toPath(), true)
            val childFiles = file.listFiles()
            if (childFiles == null || childFiles.isEmpty()) file.delete()
        } else {
            if (predicate(file)) {
                if (file.delete()) {
                    Log.trace("Deleted file: ${file.name}")
                }
            }
        }
    }
    return true
}


fun writeFile(filePath: Path, fileText: String): File = createFile(filePath, fileText)

private fun writeFile(directory: Directory, fileName: String, fileText: String): File =
    createFile(directory.getPath().resolve(fileName), fileText)


fun writeFile(filePath: Path, fileContents: ByteArray): File = createFile(filePath, String(fileContents))


private fun createFile(path: Path, fileText: String): File {
    createSubDirectories(path)
    try {
        BufferedWriter(FileWriter(path.toFile())).use { writer -> writer.write(fileText) }
    } catch (e: IOException) {
        Log.warn("Error: Unable to write to directory.")
        throw RuntimeException(e)
    }
    return path.toFile()
}

private fun formatFileName(vararg chunks: String): Array<String> {
    val cleanedChunks = Arrays.stream(chunks)
        .map { string: String -> string.replace("\\\\".toRegex(), "/") }
        .flatMap { string: String ->
            var splitArray = arrayOf(string)
            if (string.contains("/")) splitArray = string
                .split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Arrays.stream(splitArray)
        }.map { string: String ->
            val subst = "-"
            val pattern = Pattern.compile(FILE_REGEX)
            val matcher = pattern.matcher(string)
            val cleaned = matcher.replaceAll(subst).lowercase(Locale.getDefault())
            if (cleaned.length > 31) return@map cleaned.substring(0, 31) else return@map cleaned
        }.toArray()
    val combined: MutableList<String> = ArrayList()
    var combinedString = StringBuilder()
    for (index in cleanedChunks.indices) {
        combinedString.append(cleanedChunks[index])
        if (combinedString.toString().endsWith(".")) {
            if (index + 1 >= cleanedChunks.size) {
                val fileName = combinedString.toString()
                combined.add(fileName.substring(0, fileName.length - 1))
                combinedString = StringBuilder()
            }
        } else {
            combined.add(combinedString.toString())
            combinedString = StringBuilder()
        }
    }
    return combined.toTypedArray()
}

fun downloadFile(urlString: String, directory: Directory, vararg pathChunks: String): File {
    val filePath = Paths.get(directory.getPathString(), *pathChunks)
    val downloadedFile = filePath.toFile()
    try {
        FileUtils.copyURLToFile(URL(urlString), downloadedFile)
    } catch (exception: IOException) {
        throw RuntimeException(exception)
    }
    return downloadedFile
}

fun copyURLtoFile(urlString: String, file: File): Boolean {
    try {
        val url = URL(urlString)
        val input = url.openStream()
        if (file.exists()) {
            if (file.isDirectory) throw IOException("File '$file' is a directory")
            if (!file.canWrite()) throw IOException("File '$file' cannot be written")
        } else {
            val parent = file.parentFile
            if (parent != null && !parent.exists() && !parent.mkdirs()) throw IOException("File '$file' could not be created")
        }
        val output = FileOutputStream(file)
        val buffer = ByteArray(4096)
        var n: Int
        while (-1 != input.read(buffer).also { n = it }) output.write(buffer, 0, n)
        input.close()
        output.close()
        Log.trace("File '$file' downloaded successfully!")
    } catch (ioEx: IOException) {
        ioEx.printStackTrace()
        return false
    }
    return true
}

fun loadImageFromFile(file: File): Image? {
    var getImage: Image? = null
    try {
        getImage = SwingFXUtils.toFXImage(ImageIO.read(file), null)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    if (getImage == null) {
        Log.warn("Failed to load image...")
        return null
    }
    return getImage
}

fun loadImageFromURL(urlString: String): Image? {
    var getImage: Image? = null
    try {
        getImage = SwingFXUtils.toFXImage(ImageIO.read(URL(urlString)), null)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    if (getImage == null) {
        Log.warn("Failed to load image...")
        return null
    }
    return getImage
}

fun <T> save(path: Path, classType: T, gson: Gson = GSON): Boolean {
    var localPath = path
    val json: String = gson.toJson(classType)
    if (!localPath.toString().endsWith(".json")) localPath = Paths.get("$localPath.json")
    val saveFile: File = writeFile(localPath, json)
    return saveFile.exists()
}

fun <T> load(path: Path, classTypeClass: Class<T>, gson: Gson = GSON): T? {
    var localPath = path
    if (!localPath.toString().endsWith(".json")) localPath = Paths.get("$localPath.json")
    Log.info("Loading file: $localPath")
    if (!localPath.toFile().exists()) return null
    val fileContent: String = localPath.toFile().readText()
    return if (fileContent.isEmpty()) null else GSON.fromJson(fileContent, classTypeClass)
}