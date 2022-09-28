package scripts.kt.utility

import androidx.compose.ui.graphics.ImageBitmap


/* Written by IvanEOD 9/22/2022, at 10:01 AM */

data class CachedImage(
    val name: String,
    val loader: () -> ImageBitmap
) {
    val image by lazy(loader)
}

object ImageCache {

    private val cache: MutableList<CachedImage> = mutableListOf()

    fun preload(name: String, loader: () -> ImageBitmap) {
        getBitmap(name, loader)
    }

    fun getBitmap(name: String, loader: () -> ImageBitmap): ImageBitmap {
        val cached = cache.firstOrNull { it.name == name }
        if (cached != null) {
            return cached.image
        }
        val newCached = CachedImage(name, loader = loader)
        cache.add(newCached)
        return newCached.image
    }
    fun getBitmap(name: String) : ImageBitmap? {
        val cached = cache.firstOrNull { it.name == name }
        if (cached != null) {
            return cached.image
        }
        return null
    }

}