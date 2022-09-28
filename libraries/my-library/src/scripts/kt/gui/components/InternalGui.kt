package scripts.kt.gui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.twotone.KeyboardArrowRight
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch
import org.tribot.script.sdk.script.ScriptRuntimeInfo
import scripts.kt.gui.AsyncImage
import scripts.kt.gui.ScriptIcon
import scripts.kt.gui.ScriptText
import scripts.kt.gui.components.alert.ScriptGuiAlert
import scripts.kt.gui.theme.ScriptColorProvider
import scripts.kt.gui.theme.ScriptSurface
import scripts.kt.gui.theme.ScriptTheme


/* Written by IvanEOD 9/28/2022, at 10:22 AM */

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GuiWindow(
    title: String = "TRiBot: ${ScriptRuntimeInfo.getScriptName()}",
    guiScope: GuiScope,
    icon: ScriptIcon = ScriptIcon.TRiBotLogo,
    size: DpSize = DpSize(800.dp, 600.dp),
    onCloseRequest: () -> Unit = {},
) {

    val scope = remember { guiScope.scope }
    val scrollbarState = remember { guiScope.scrollbarState }
    var count by remember {
        mutableStateOf(0)
    }

    var hoveringClose by remember { mutableStateOf(false) }
    val animatedCloseButtonColor = animateColorAsState(
        if (hoveringClose) Color(0xFFE81123) else Color(0, 0, 0, 0),
    )

    val iconPainter = icon.painter()
    val rememberIconPainter = remember { iconPainter }

    Window(
        onCloseRequest = onCloseRequest,
        icon = iconPainter,
        title = title,
        state = rememberWindowState(size = size),
        transparent = true,
        undecorated = true,
        resizable = false,
        onKeyEvent = {
            scope.launch {
                if (it.type == KeyEventType.KeyDown && it.key == Key.DirectionDown) {
                    count++
                    scrollbarState.scrollState.animateScrollBy((50 * count).toFloat())

                } else if (it.type == KeyEventType.KeyDown && it.key == Key.DirectionUp) {
                    count++
                    scrollbarState.scrollState.animateScrollBy((-50 * count).toFloat())
                } else {
                    count = 0
                }
            }
            true
        }
    ) {
        ScriptTheme(true) {
            ScriptSurface(
                modifier = Modifier.fillMaxSize()
                    .border(0.15.dp,
                        color = ScriptColorProvider.colors.blackOutlineColor,
                        shape = RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxWidth().height(30.dp)) {
                        ScriptSurface(Modifier.fillMaxSize(), color = ScriptColorProvider.colors.blackOutlineColor) {
                            WindowDraggableArea(Modifier.fillMaxSize()) {
                                Box(Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        load = { rememberIconPainter },
                                        painterFor = { remember { it } },
                                        contentDescription = "Icon",
                                        modifier = Modifier.size(30.dp).align(Alignment.CenterStart)
                                            .padding(start = 5.dp)
                                    )
                                    ScriptText(
                                        text = title,
                                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 35.dp)
                                    )
                                    IconButton(modifier = Modifier
                                        .fillMaxHeight()
                                        .align(Alignment.CenterEnd)
                                        .background(color = animatedCloseButtonColor.value)
                                        .onPointerEvent(PointerEventType.Enter) {
                                            hoveringClose = true
                                        }
                                        .onPointerEvent(PointerEventType.Exit) {
                                            hoveringClose = false
                                        },
                                        onClick = {
                                            onCloseRequest()
                                        }
                                    ) {
                                        ScriptIcon(Icons.Filled.Close, "")
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        Modifier.fillMaxSize()
                    ) {
                        GuiLayout(guiScope)
                    }
                }
            }
        }
    }

}

@Composable
internal fun GuiLayout(
    scope: GuiScope,
) {
    val guiScope = remember { scope }
    val scrollbarState by remember {
        derivedStateOf {
            scope.scrollbarState
        }
    }
    val scriptScaffoldState by remember {
        derivedStateOf {
            scope.desktopScaffoldState
        }
    }

    val screens by remember {
        derivedStateOf {
            scope.screens
        }
    }


    val navigation by remember {
        derivedStateOf {
            guiScope.navigation
        }
    }


    val currentScreen by remember {
        derivedStateOf {
            navigation.currentScreen
        }
    }

    val state by remember {
        derivedStateOf {
            guiScope.state
        }
    }

    val alertState by remember {
        derivedStateOf {
            guiScope.alertState
        }
    }

    var selected by remember {
        mutableStateOf(currentScreen)
    }

    val onPrimaryPage by remember {
        derivedStateOf {
            currentScreen.info.primary
        }
    }

    val unsavedChangesWarningEnabled = remember { mutableStateOf(false) }


    /**
     * For Navigation Rail Animation. It can be hidden or shown.
     */
    var visible by remember {
        mutableStateOf(true)
    }
    val width by animateDpAsState(
        targetValue = if (visible) 80.dp else 0.dp,
        animationSpec = tween(500)
    )

    val offset by animateOffsetAsState(
        targetValue = if (visible) Offset.Zero else Offset(-80f, 0f),
        animationSpec = tween(500)
    )

    LaunchedEffect(visible) {
        println(currentScreen)
    }

    ScriptTheme(true) {
        ScriptScaffold(
            scaffoldState = scriptScaffoldState,
            topBar = {
                // Optionally have a top bar
            },
            bottomBar = {

                // Optionally have a bottom bar

            },
            floatingActionButton = {

                Column(
                    modifier = Modifier.animateContentSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                ) {
                    currentScreen.floatingAction(currentScreen)

                    Spacer(modifier = Modifier.height(10.dp))

                    ExtendedFloatingActionButton(text = { ScriptText("Start Script") },
                        icon = { ScriptIcon(Icons.TwoTone.PlayArrow, "") },
                        onClick = {
                            if (unsavedChangesWarningEnabled.value) {
                                guiScope.dispatchAlert(
                                    "Unsaved Changes",
                                    "You have unsaved changes, would you like to go back so you can save them?",
                                    "Yes",
                                    "No",
                                    { },
                                    { guiScope.closeGui() }
                                )
                            } else {
                                guiScope.closeGui()
                            }
                        }
                    )
                }
            },
            navigationRail = {
                if (onPrimaryPage) {
                    NavigationRail(
                        modifier = Modifier.width(width).offset(offset.x.dp, offset.y.dp)
                            .background(ScriptColorProvider.colors.blackOutlineColor),
                        backgroundColor = ScriptColorProvider.colors.blackOutlineColor
                    ) {
                        screens.forEach { screen ->
                            if (screen.info.primary) {
                                NavigationRailItem(
                                    icon = { ScriptIcon(screen.info.icon, "", Modifier.size(25.dp)) },
                                    label = {
                                        ScriptText(
                                            screen.info.title,
                                            color = if (selected == screen) ScriptColorProvider.colors.primary else ScriptColorProvider.colors.textPrimary,
                                        )
                                    },
                                    selected = selected == screen,
                                    onClick = {
                                        selected = screen
                                        navigation.navigate(screen)
                                    },
                                    alwaysShowLabel = true,
                                )
                            }
                        }
                    }
                } else {
                    NavigationRail(
                        modifier = Modifier.width(width).offset(offset.x.dp, offset.y.dp)
                            .background(ScriptColorProvider.colors.blackOutlineColor),
                        backgroundColor = ScriptColorProvider.colors.blackOutlineColor
                    ) {
                        NavigationRailItem(
                            icon = { ScriptIcon(Icons.Filled.ArrowBack, "") },
                            label = { ScriptText("Back") },
                            selected = false,
                            onClick = {
                                navigation.navigateBack()
                            },
                            alwaysShowLabel = true,
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = scriptScaffoldState.snackbarHostState,
                    modifier = Modifier.fillMaxWidth(),
                    snackbar = {
                        Snackbar(
                            snackbarData = it,
                            backgroundColor = ScriptColorProvider.colors.blackOutlineColor,
                            contentColor = ScriptColorProvider.colors.textPrimary,
                            actionColor = ScriptColorProvider.colors.secondary,
                        )
                    }
                )
            },
            verticalScrollBar = {
                VerticalScrollbar(
                    adapter = scrollbarState.scrollbarAdapter
                )
            },
            horizontalScrollBar = {

                //Optionally have horizontal scrolling

//                HorizontalScrollbar(
//                    adapter = rememberScrollbarAdapter(scrollbarState.scrollState)
//                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(color = ScriptColorProvider.colors.blackOutlineColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().clip(
                        RoundedCornerShape(
                            topStart = 10.dp
                        )
                    ).background(color = ScriptColorProvider.colors.background),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollbarState.scrollState)
                        .background(color = ScriptColorProvider.colors.chatBackground)
                    ) {
                        AnimatedVisibility(navigation.currentScreen.info.primary.not()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().height(20.dp)
                                    .background(color = ScriptColorProvider.colors.chatBackground),

                                ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(start = 16.dp, top = 2.dp, bottom = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    navigation.backStack.forEachIndexed { index, screen ->
                                        ScriptText(
                                            screen.toString(),
                                            modifier = Modifier.clickable {
                                                navigation.navigate(screen)
                                            },
                                            fontSize = 10.sp
                                        )
                                        ScriptIcon(
                                            imageVector = Icons.TwoTone.KeyboardArrowRight,
                                            modifier = Modifier.size(20.dp).padding(horizontal = 5.dp)
                                        )
                                    }
                                    ScriptText(
                                        navigation.currentScreen.info.title,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 5.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        NavigationHost(navigation) {
                            screens.forEach { screen ->
                                composable(screen) {
                                    Box(
                                        modifier = Modifier.defaultMinSize(720.dp,
                                            if (screen.info.primary) 540.dp else 520.dp).width(720.dp)
                                    ) {
                                        screen.render(guiScope)
                                    }
                                }
                            }
                        }.build()
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (alertState.showing.value) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                ScriptGuiAlert(guiScope.alertState,
                                    guiScope::alertConfirmed,
                                    guiScope::alertCancelled)
                            }
                        }
                    }
                }
            }
        }
    }

}
