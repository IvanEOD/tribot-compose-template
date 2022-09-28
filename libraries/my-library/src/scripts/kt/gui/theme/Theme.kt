package scripts.kt.gui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


/* Written by IvanEOD 9/14/2022, at 7:02 AM */
private val LightColorPalette = ScriptColorPalette(
    primary = design_default_color_primary,
    primaryVariant = design_default_color_primary_variant,
    secondary = design_default_color_secondary,
    secondaryVariant = design_default_color_secondary_variant,
    textPrimary = Color.Black,
    textSecondary = Color.DarkGray,
    background = design_default_color_background,
    chatTopBar = Color(0xFFFFFFFF),
    chatBackground = Color(0xFFFFFFFF),
    chatEditor = Color(0xFFecedef),
    secondaryBackground = design_default_color_secondary_background,
    onSecondaryBackground = design_default_color_on_secondary,
    surface = design_default_color_surface,
    brand = brand,
    error = design_default_color_error,
    onPrimary = design_dark_default_color_on_primary,
    onSecondary = design_default_color_on_secondary,
    onBackground = design_default_color_on_background,
    onSurface = design_default_color_on_surface,
    onError = design_default_color_on_error,
    appBarColor = design_default_app_bar_color_light,
    appBarColor2 = design_default_app_bar_color_light,
    scriptBackgroundOne = design_default_app_bar_color_light,
    linkColor = design_default_link_color,
    tabsBackgroundColor = design_default_color_secondary_background,
    tabSelectedColor = design_default_tab_selected_color,
    textFieldContentColor = text_field_content_color_light,
    settingsBackground = Color(0xFFFFFFFF),
    isLight = true
)

private val DarkColorPalette = ScriptColorPalette(
    primary = design_default_color_primary_dark,
    primaryVariant = design_dark_default_color_primary_variant,
    secondary = design_dark_default_color_secondary,
    secondaryVariant = design_dark_default_color_secondary_variant,
    textPrimary = Color.White,
    textSecondary = Color.LightGray,
    background = design_dark_default_color_background,
    chatTopBar = Color(0xFF303136),
    chatBackground = Color(0xFF363940),
    chatEditor = Color(0xFF2a2b2f),
    secondaryBackground = design_dark_default_color_secondary_background,
    onSecondaryBackground = design_dark_default_color_on_background,
    surface = design_dark_default_color_surface,
    brand = exo_white,
    error = design_dark_default_color_error,
    onPrimary = design_default_color_on_primary,
    onSecondary = design_dark_default_color_on_secondary,
    onBackground = design_dark_default_color_on_background,
    onSurface = design_dark_default_color_on_surface,
    onError = design_dark_default_color_on_error,
    appBarColor = design_default_app_bar_color_dark,
    appBarColor2 = design_default_app_bar_color_dark2,
    scriptBackgroundOne = design_default_app_bar_color_dark,
    linkColor = design_default_link_color,
    tabsBackgroundColor = Color(0xFF2a2b2f),
    tabSelectedColor = Color(0xFF4f535c),
    textFieldContentColor = text_field_content_color_Dark,
    settingsBackground = Color(0xFF363940),
    isLight = false
)

@Composable
fun ScriptTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette

    ProvideScriptColors(colors) {

        MaterialTheme(
            colors = Colors(
                primary = colors.primary,
                primaryVariant = colors.primaryVariant,
                secondary = colors.secondary,
                secondaryVariant = colors.secondaryVariant,
                background = colors.background,
                surface = colors.surface,
                error = colors.error,
                onPrimary = colors.onPrimary,
                onSecondary = colors.onSecondary,
                onBackground = colors.onBackground,
                onSurface = colors.onSurface,
                onError = colors.onError,
                isLight = colors.isLight
            ),
//            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object ScriptColorProvider {
    val colors: ScriptColorPalette
        @Composable
        get() = LocalScriptColor.current
}


val ScriptColorPalette.primarySurface: Color get() = if (isLight) primary else surface

fun ScriptColorPalette.contentColorFor(backgroundColor: Color): Color {
    return when (backgroundColor) {
        primary -> onPrimary
        primaryVariant -> onPrimary
        secondary -> onSecondary
        appBarColor, appBarColor2 -> onSecondaryBackground
        secondaryBackground -> onSecondaryBackground
        secondaryVariant -> onSecondary
        background -> onBackground
        surface -> onSurface
        error -> onError
        else -> Color.Unspecified
    }
}

/**
 * Discord custom Color Palette
 */
@Stable
class ScriptColorPalette(
    primary: Color = Color(0xFF7FB300),
//    primary: Color = Color(0xFFBB86FC),
    primaryVariant: Color = Color(0xFF82B300),
//    primaryVariant: Color = Color(0xFF3700B3),
    secondary: Color = Color(0xFF03DAC6),
    secondaryVariant: Color = secondary,
    textPrimary: Color = Color(0xFF5865f2),
    textSecondary: Color,
    background: Color = Color(0xFF2f3238),
    chatTopBar: Color,
    chatBackground: Color,
    chatEditor: Color,
    secondaryBackground: Color,
    onSecondaryBackground: Color,
    surface: Color = Color(0xFF121212),
//    brand: Color = Color(0xFF5865f2),
    brand: Color = Color(0xFF5865f2),
    error: Color = Color(0xFFCF6679),
    onPrimary: Color = Color.Black,
    onSecondary: Color = Color.Black,
    onBackground: Color = Color.White,
    onSurface: Color = Color.White,
    onError: Color = Color.Black,
    appBarColor: Color,
    appBarColor2: Color,
    scriptBackgroundOne: Color,
    linkColor: Color,
    tabsBackgroundColor: Color,
    tabSelectedColor: Color,
    textFieldContentColor: Color,
    settingsBackground: Color,
    isLight: Boolean = false,

    blackOutlineColor: Color = Color(0xFF111418),
    textFieldBorderStroke: BorderStroke = BorderStroke(2.dp, background),
    textFieldShape: Shape = RoundedCornerShape(8.dp)


) {
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
        internal set
    var primaryVariant by mutableStateOf(primaryVariant, structuralEqualityPolicy())
        internal set
    var secondary by mutableStateOf(secondary, structuralEqualityPolicy())
        internal set
    var secondaryVariant by mutableStateOf(secondaryVariant, structuralEqualityPolicy())
        internal set
    var textPrimary by mutableStateOf(textPrimary)
        private set
    var textSecondary by mutableStateOf(textSecondary)
        private set
    var background by mutableStateOf(background, structuralEqualityPolicy())
        internal set
    var chatTopBar by mutableStateOf(chatTopBar, structuralEqualityPolicy())
        internal set
    var chatBackground by mutableStateOf(chatBackground, structuralEqualityPolicy())
        internal set
    var chatEditor by mutableStateOf(chatEditor, structuralEqualityPolicy())
        internal set
    var secondaryBackground by mutableStateOf(secondaryBackground, structuralEqualityPolicy())
        internal set
    var surface by mutableStateOf(surface, structuralEqualityPolicy())
        internal set
    var brand by mutableStateOf(brand, structuralEqualityPolicy())
        internal set
    var error by mutableStateOf(error, structuralEqualityPolicy())
        internal set
    var onPrimary by mutableStateOf(onPrimary, structuralEqualityPolicy())
        internal set
    var onSecondary by mutableStateOf(onSecondary, structuralEqualityPolicy())
        internal set
    var onSecondaryBackground by mutableStateOf(onSecondaryBackground, structuralEqualityPolicy())
        internal set
    var onBackground by mutableStateOf(onBackground, structuralEqualityPolicy())
        internal set
    var onSurface by mutableStateOf(onSurface, structuralEqualityPolicy())
        internal set
    var onError by mutableStateOf(onError, structuralEqualityPolicy())
        internal set
    var isLight by mutableStateOf(isLight, structuralEqualityPolicy())
        internal set
    var appBarColor by mutableStateOf(appBarColor, structuralEqualityPolicy())
        internal set
    var appBarColor2 by mutableStateOf(appBarColor2, structuralEqualityPolicy())
        internal set
    var scriptBackgroundOne by mutableStateOf(scriptBackgroundOne, structuralEqualityPolicy())
        internal set
    var linkColor by mutableStateOf(linkColor, structuralEqualityPolicy())
        internal set
    var tabsBackgroundColor by mutableStateOf(tabsBackgroundColor, structuralEqualityPolicy())
        internal set
    var textFieldContentColor by mutableStateOf(textFieldContentColor, structuralEqualityPolicy())
        internal set
    var tabSelectedColor by mutableStateOf(tabSelectedColor, structuralEqualityPolicy())
        internal set
    var settingsBackground by mutableStateOf(settingsBackground, structuralEqualityPolicy())
        internal set

    var textFieldBorder by mutableStateOf(textFieldBorderStroke, structuralEqualityPolicy())
        internal set

    var textFieldShape by mutableStateOf(textFieldShape, structuralEqualityPolicy())
        internal set
    
    var blackOutlineColor by mutableStateOf(blackOutlineColor, structuralEqualityPolicy())
        internal set

    fun update(other: ScriptColorPalette) {
        primary = other.primary
        primaryVariant = other.primaryVariant
        secondary = other.secondary
        secondaryVariant = other.secondaryVariant
        background = other.background
        secondaryBackground = other.secondaryBackground
        onSecondaryBackground = other.onSecondaryBackground
        surface = other.surface
        error = other.error
        onPrimary = other.onPrimary
        onSecondary = other.onSecondary
        onBackground = other.onBackground
        onSurface = other.onSurface
        onError = other.onError
        isLight = other.isLight
        appBarColor = other.appBarColor
        appBarColor2 = other.appBarColor2
        scriptBackgroundOne = other.scriptBackgroundOne
        linkColor = other.linkColor
        tabsBackgroundColor = other.tabsBackgroundColor
        textFieldContentColor = other.textFieldContentColor
        tabSelectedColor = other.tabSelectedColor
        textFieldBorder = other.textFieldBorder
        textFieldShape = other.textFieldShape
        blackOutlineColor = other.blackOutlineColor
    }
}

@Composable
fun ProvideScriptColors(
    colors: ScriptColorPalette,
    content: @Composable () -> Unit
) {
    val colorPalette = remember { colors }
    colorPalette.update(colors)
    CompositionLocalProvider(LocalScriptColor provides colorPalette, content = content)
}

private val LocalScriptColor = staticCompositionLocalOf<ScriptColorPalette> {
    error("No ScriptColorPalette provided")
}

fun debugColors(
    darkTheme: Boolean,
    debugColor: Color = Color.Red
) = Colors(
    primary = debugColor,
    primaryVariant = debugColor,
    secondary = debugColor,
    secondaryVariant = debugColor,
    background = debugColor,
    surface = debugColor,
    error = debugColor,
    onPrimary = debugColor,
    onSecondary = debugColor,
    onBackground = debugColor,
    onSurface = debugColor,
    onError = debugColor,
    isLight = !darkTheme
)