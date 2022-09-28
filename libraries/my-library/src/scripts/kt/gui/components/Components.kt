package scripts.kt.gui

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import scripts.kt.gui.components.ScriptIcon
import scripts.kt.gui.theme.ScriptColorProvider
import scripts.kt.gui.theme.ScriptSurface
import scripts.kt.gui.theme.primarySurface
import java.io.File
import java.io.IOException
import java.net.URL


/* Written by IvanEOD 9/28/2022, at 10:13 AM */
@Composable
fun ScriptText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ScriptColorProvider.colors.onSurface,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    fontDecoration: TextDecoration? = null,
    fontAlignment: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = fontDecoration,
        textAlign = fontAlignment,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

@Composable
fun ScriptIcon(
    imageVector: ImageVector,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
    )
}


@Composable
fun ScriptIcon(
    painter: Painter,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

@Composable
fun ScriptIcon(
    iconPainter: ScriptIcon,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
) {
    ScriptIcon(iconPainter.painter, contentDescription, modifier)
}

@Composable
fun ScriptIcon(
    iconPainter: @Composable () -> Painter,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
) {
    ScriptIcon(iconPainter(), contentDescription, modifier)
}

private const val DividerAlpha = 0.06f

@Composable
fun ScriptDivider(
    modifier: Modifier = Modifier,
    color: Color = ScriptColorProvider.colors.onSurface.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
) {
    Divider(modifier = modifier, color = color, thickness = thickness, startIndent = startIndent)
}

@Composable
fun ScriptAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = ScriptColorProvider.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
) {
    ScriptSurface(color = backgroundColor, contentColor = contentColor, elevation = elevation) {
        TopAppBar(title, modifier, navigationIcon, actions, backgroundColor, contentColor, elevation)
    }
}

@Composable
fun TitleCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.widthIn(400.dp, 800.dp)
            .padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 10.dp),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(
                start = 10.dp,
                top = 10.dp,
                bottom = 10.dp,
                end = 10.dp
            )
        ) {
            ScriptText(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
            content()
        }
    }
}

/**
 * A full radio button solution as an elevated row. [option] represents what this particular button is, with text populating
 * via "option.toString()". When the row is clicked [currentSelectionState]'s value will be set to [option].
 *
 * [dropDownContent] is an optional composable that will display as part of this row card if the radio button is selected.
 */
@Composable
fun <T> RadioButtonBar(
    currentSelectionState: MutableState<T>,
    option: T,
    dropDownContent: (@Composable () -> Unit)? = null
) {
    val (selectedOption, onOptionSelected) = remember { currentSelectionState }

    Box(Modifier.padding(bottom = 3.dp)) {
        ScriptSurface(elevation = 1.dp) {
            Column(
                // When the column expands, give it a nice animation
                Modifier.animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ).fillMaxWidth()
            ) {

                // Make the whole row clickable. We don't want the dropdown part to be clickable, though.
                Row(Modifier.height(35.dp).fillMaxWidth().clickable { onOptionSelected(option) }) {
                    RadioButton(
                        onClick = { onOptionSelected(option) },
                        selected = selectedOption == option,
                        modifier = Modifier.align(Alignment.CenterVertically).size(20.dp).padding(start = 15.dp)
                    )
                    ScriptText(
                        option.toString(),
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 20.dp),
                        fontSize = 15.sp
                    )
                }

                // Render the dropdown part if this radio button is selected
                if (dropDownContent != null && selectedOption == option) {
                    dropDownContent()
                }
            }
        }
    }
}

@Composable
fun <T> DropdownSelection(
    currentSelectionIndex: MutableState<Int>,
    options: List<T>,
    optionToString: (T) -> String
) {
    val (selectedOption, setSelectedOption) = remember { currentSelectionIndex }
    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    val (textFieldValue, setTextFieldValue) = remember { mutableStateOf(if (options.isNotEmpty()) optionToString(options[selectedOption]) else "") }

    Box(Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {

        ScriptSurface(
            modifier = Modifier.fillMaxSize().clickable {
                setExpanded(!expanded)
            },
            shape = RoundedCornerShape(8.dp),
            color = ScriptColorProvider.colors.scriptBackgroundOne,
            border = BorderStroke(1.dp, ScriptColorProvider.colors.settingsBackground),

            ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ScriptText(
                    text = textFieldValue,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp).fillMaxWidth(0.9f).align(Alignment.CenterVertically)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 10.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
            modifier = Modifier.width(200.dp)
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(onClick = {
                    setSelectedOption(index)
                    setTextFieldValue(optionToString(option))
                    setExpanded(false)
                }) {
                    ScriptText(text = optionToString(option))
                }
            }
        }
    }
}

@Composable
fun FavoriteStarToggleButton(
    favorite: MutableState<Boolean>,
    onClick: () -> Unit = { favorite.value = !favorite.value }
) {
    val isFavorite by remember { favorite }

    IconButton(
        onClick = { onClick() },
        modifier = Modifier.size(20.dp),
        content = {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Favorite",
                tint = if (isFavorite) ScriptColorProvider.colors.primary else ScriptColorProvider.colors.tabsBackgroundColor
            )
        }
    )
}


@Composable
fun ToggleableTitledSection(
    title: String,
    shape: Shape = RoundedCornerShape(4.dp),
    enabledState: MutableState<Boolean>,
    onToggled: (Boolean) -> Unit = { _ -> },
    content: @Composable () -> Unit
) {
    val (enabled, setEnabled) = remember { enabledState }

    Box(Modifier.padding(bottom = 3.dp)) {
        ScriptSurface(
            elevation = 0.5.dp,
            shape = shape,
        ) {
            Column(
                // When the column expands, give it a nice animation
                Modifier.animateContentSize(
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = LinearOutSlowInEasing
                    )
                ).fillMaxWidth()
            ) {

                // Make the whole row clickable. We don't want the dropdown part to be clickable, though.
                Row(
                    modifier = Modifier.height(35.dp).fillMaxWidth().clickable { setEnabled(!enabled) },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScriptText(
                        title,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 20.dp),
                        fontSize = 15.sp
                    )
                    Switch(
                        checked = enabled,
                        onCheckedChange = {
                            setEnabled(it)
                            onToggled(it)
                        },
                        modifier = Modifier.align(Alignment.CenterVertically).padding(end = 20.dp)
                    )
                }

                // Render the dropdown part if this radio button is selected
                if (enabled) {
                    ScriptDivider(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
                    )
                    content()
                }
            }
        }
    }
}


@Composable
fun ResponsiveGrid(components: List<@Composable () -> Unit>) {
    val scrollState = rememberScrollState(0)

    ScriptSurface {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            if (maxWidth >= 800.dp) {
                Column(Modifier.fillMaxWidth().verticalScroll(scrollState, true).padding(end = 20.dp)) {
                    Row(Modifier.widthIn(800.dp, 1600.dp)) {
                        Column(Modifier.fillMaxWidth().weight(1f)) {
                            components.forEachIndexed { index, func ->
                                if (index % 2 == 0) {
                                    func()
                                }
                            }
                        }
                        Column(Modifier.fillMaxWidth().weight(1f)) {
                            components.forEachIndexed { index, func ->
                                if (index % 2 != 0) {
                                    func()
                                }
                            }
                        }
                    }

                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState)
                )

            } else {
                Box {
                    Column(Modifier.fillMaxWidth().verticalScroll(scrollState, true).padding(end = 20.dp)) {
                        components.forEach { it() }
                    }

                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState)
                    )
                }
            }
        }
    }
}

@Composable
fun TransitionView(
    visible: State<Boolean>,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    AnimatedVisibility(
        visible = visible.value,
        enter = slideInVertically {
            with(density) { (-40).dp.roundToPx() }
        } + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically(targetOffsetY = { -10 }) + shrinkVertically() + fadeOut()
    ) {
        content()
    }
}


@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: IOException) {
                // instead of printing to console, you can also write this to log,
                // or show some error placeholder
                e.printStackTrace()
                null
            }
        }
    }

    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}


fun loadImageFromFile(file: File): ImageBitmap = file.inputStream().buffered().use(::loadImageBitmap)
fun loadImageFromUrl(url: String): ImageBitmap = URL(url).openStream().buffered().use(::loadImageBitmap)
