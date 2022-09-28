package scripts.kt.gui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.Cursor
import org.tribot.script.sdk.query.Query
import org.tribot.script.sdk.types.definitions.ItemDefinition
import scripts.kt.gui.theme.ScriptColorProvider
import scripts.kt.utility.AssetManager
import scripts.kt.utility.ImageCache
import java.awt.image.BufferedImage
import java.net.URLEncoder
import java.util.*


/* Written by IvanEOD 9/23/2022, at 8:31 PM */

fun getItemDefinition(name: String): ItemDefinition? {
    if (name.isEmpty()) return null
    return Query.itemDefinitions()
        .nameEquals(name.replace("_", " "))
        .isNotPlaceholder
        .isNotNoted
        .distinctByName()
        .findFirst()
        .orElse(null)
        ?: Query.itemDefinitions()
            .nameStartsWith(name.replace("_", " "))
            .isNotPlaceholder
            .isNotNoted
            .distinctByName()
            .findFirst()
            .orElse(null)
        ?: Query.itemDefinitions()
            .nameContains(name.replace("_", " "))
            .isNotPlaceholder
            .isNotNoted
            .distinctByName()
            .findFirst()
            .orElse(null)
}

fun isStackable(name: String) : Boolean {
    val definition = getItemDefinition(name)
    return definition?.isStackable ?: false
}

fun String.toTitleCase(): String {
    return lowercase().split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}

internal fun String.urlEncode(): String {
    return URLEncoder.encode(this, "UTF-8")
}

internal fun getGithubImagePainter(iconName: String): BitmapPainter {
    return BitmapPainter(ImageCache.getBitmap(iconName) { AssetManager.loadGithubImageToBitmap(iconName) })
}

fun java.awt.Image.toBufferedImage(width: Int = 100, height: Int = 100): BufferedImage {
    if (this is BufferedImage) return this
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = bufferedImage.createGraphics()
    graphics.drawImage(this, 0, 0, null)
    graphics.dispose()
    return bufferedImage
}

fun java.awt.Image.toComposeImageBitmap(): ImageBitmap = this.toBufferedImage().toComposeImageBitmap()



@Composable
fun Modifier.thinOutline() = border(BorderStroke(0.15.dp, ScriptColorProvider.colors.blackOutlineColor), RoundedCornerShape(8.dp))

@Composable
fun Modifier.indexedListBackground(
    index: Int,
    odds: Color = ScriptColorProvider.colors.secondaryBackground,
    evens: Color = ScriptColorProvider.colors.chatBackground
) = background(if (index % 2 == 0) evens else odds)

fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))


val windowFrameIcons = arrayOf(
    "Agility",
    "Attack",
    "Construction",
    "Cooking",
    "Crafting",
    "Defence",
    "Farming",
    "Firemaking",
    "Fishing",
    "Fletching",
    "Herblore",
    "Hitpoints",
    "Hunter",
    "Magic",
    "Mining",
    "Prayer",
    "Ranged",
    "Runecraft",
    "Slayer",
    "Smithing",
    "Strength",
    "Thieving",
    "Woodcutting",
)
