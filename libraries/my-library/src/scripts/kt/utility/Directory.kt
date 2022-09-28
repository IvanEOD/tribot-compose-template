package scripts.kt.utility

import com.google.gson.Gson
import org.tribot.script.sdk.MyPlayer
import org.tribot.script.sdk.Tribot
import org.tribot.script.sdk.script.ScriptRuntimeInfo
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.io.path.name


/* Written by IvanEOD 8/31/2022, at 10:56 AM */
fun interface Directory {

    fun getDirectoryPath(): Path
    fun getLocksDirectoryPath(): Path = getDirectoryPath().resolve("locks")
    fun getPath(vararg pathChunks: String): Path {
        val path = Paths.get(getDirectoryPath().toString(), *pathChunks)
        createSubDirectories(path)
        return path
    }
    fun getFile(vararg pathChunks: String): File = getPath(*pathChunks).toFile()
    fun getPathString(): String = getDirectoryPath().toString()
    fun exists(vararg pathChunks: String) = getFile(*pathChunks).exists()
    fun loadFileAsString(vararg pathChunks: String): String = getFile(*pathChunks).readText()
    fun <T> save(classToSave: T, vararg pathChunks: String) = save(getPath(*pathChunks), classToSave)
    fun <T> save(classToSave: T, gson: Gson = Gson(), vararg pathChunks: String) = save(getPath(*pathChunks), classToSave, gson)
    fun <T> load(classTypeClass: Class<T>, vararg pathChunks: String) = load(getPath(*pathChunks), classTypeClass)

    fun copyUrlToFile(url: String, vararg pathChunks: String) = copyURLtoFile(url, getFile(*pathChunks))
    fun loadImageFromFile(vararg pathChunks: String) = loadImageFromFile(getFile(*pathChunks))
    fun writeToFile(text: String, vararg pathChunks: String) = writeFile(getPath(*pathChunks), text)
    fun clean() = deleteFilesOlderThan(5, TimeUnit.DAYS, getDirectoryPath(), true)
    fun cleanLocks() = deleteFilesOlderThan(5, TimeUnit.DAYS, getLocksDirectoryPath(), true)
    fun clear() = deleteFiles( {true}, getDirectoryPath(), true)
    fun getFilePathNoOverwrite(vararg pathChunks: String): Path {
        val path = getPath(*pathChunks)
        if (path.toFile().exists()) {
            val name = path.fileName.toString()
            val extension = name.substring(name.lastIndexOf("."))
            val nameNoExtension = name.substring(0, name.lastIndexOf("."))
            val newPath = path.parent.resolve("${nameNoExtension}_${System.currentTimeMillis()}$extension")
            return getFilePathNoOverwrite(newPath.toString())
        }
        if (!path.toFile().exists()) {
            path.toFile().mkdir()
        }
        return path
    }
    fun getDirectoryNoOverwrite(vararg pathChunks: String): Path {
        val path = getPath(*pathChunks)
        if (path.toFile().exists()) {
            val newPath = path.parent.resolve("${path.name}_${System.currentTimeMillis()}")
            return getDirectoryNoOverwrite(newPath.toString())
        }
        if (!path.toFile().exists()) {
            path.toFile().mkdir()
        }
        return path
    }

    companion object {
        @JvmStatic
        val TRiBot = Directory { Tribot.getDirectory().toPath() }
        @JvmStatic
        val Root = Directory { createTRiBotPath() }
        @JvmStatic
        val Script = Directory { createTRiBotPath("scripts", ScriptRuntimeInfo.getScriptName()) }
        @JvmStatic
        val Account = Directory { createTRiBotPath("accounts", MyPlayer.getUsername()) }
        @JvmStatic
        val Settings = Directory { Paths.get(Tribot.getDirectory().toPath().toString(), "settings")}

    }


}