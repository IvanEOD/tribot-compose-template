package scripts.kt.gui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import scripts.kt.gui.components.GuiScreen
import scripts.kt.gui.components.GuiScreenInfo
import scripts.kt.gui.components.GuiScreenScope
import scripts.kt.gui.components.ScriptIcon
import scripts.kt.gui.theme.ScriptColorProvider
import scripts.kt.gui.theme.ScriptSurface


/* Written by IvanEOD 9/28/2022, at 10:40 AM */

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@DslMarker
annotation class GuiBuilderDsl

@GuiBuilderDsl
abstract class AbstractGuiBuilder<T> {
    internal abstract fun build(): T
}

class GuiBuilder(
    private val title: String,
    private val icon: ScriptIcon = ScriptIcon.TRiBotLogo,
) : AbstractGuiBuilder<ScriptGui>() {
    private val screens: MutableList<GuiScreen> = mutableListOf()

    private var onGuiLoad: () -> Unit = {}
    private var onGuiClose: () -> Unit = {}

    fun onGuiLoad(block: () -> Unit) {
        onGuiLoad = block
    }

    fun onGuiClose(block: () -> Unit) {
        onGuiClose = block
    }

    fun Screen(
        title: String,
        icon: ScriptIcon,
        navigationKey: String = title,
        init: @ScreenBuilderDsl ScreenBuilder.() -> Unit,
    ) = initScreen(title, icon, true, navigationKey, init)

    fun Screen(
        screen: GuiScreen
    ) : GuiScreen {
        screens.add(screen)
        return screen
    }

    internal fun initScreen(
        title: String,
        icon: ScriptIcon,
        primary: Boolean,
        navigationKey: String = title,
        init: ScreenBuilder.() -> Unit,
    ): GuiScreen {
        val builder = ScreenBuilder(title, icon, primary, navigationKey, this)
        builder.init()
        val screen = builder.build()
        screens.add(screen)
        return screen
    }


    override fun build(): ScriptGui {
        return ScriptGui(title, icon, screens, onGuiClose)
    }
}


@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@DslMarker
annotation class ScreenBuilderDsl

@ScreenBuilderDsl
class ScreenBuilder internal constructor(
    private val title: String,
    private val icon: ScriptIcon = ScriptIcon.TRiBotLogo,
    private val primary: Boolean = true,
    private val navigationKey: String,
    private val guiBuilder: GuiBuilder = GuiBuilder("", ScriptIcon.TRiBotLogo),
) {

    private var builderOnGuiClosed: () -> Unit = {}
    private val screens = mutableListOf<GuiScreen>()

    private var mainFrame: @Composable GuiScreenScope.() -> Unit = {}
    private var builderFloatingAction: @Composable GuiScreenScope.() -> Unit = {}
    private var hasSetFloatingAction = false
    private var extendableLeftFrameContent: @Composable GuiScreenScope.() -> Unit = {}
    private var hasSetLeftFrame = false
    private var extendableRightFrameContent: @Composable GuiScreenScope.() -> Unit = {}
    private var hasSetRightFrame = false

    fun onGuiClosed(onGuiClosed: () -> Unit) {
        builderOnGuiClosed = onGuiClosed
    }

    fun MainFrame(content: @Composable GuiScreenScope.() -> Unit) {
        mainFrame = content
    }

    fun FloatingAction(content: @Composable GuiScreenScope.() -> Unit) {
        builderFloatingAction = content
        hasSetFloatingAction = true
    }

    fun LeftFrame(content: @Composable GuiScreenScope.() -> Unit) {
        extendableLeftFrameContent = content
        hasSetLeftFrame = true
    }

    fun RightFrame(content: @Composable GuiScreenScope.() -> Unit) {
        extendableRightFrameContent = content
        hasSetRightFrame = true
    }

    fun Screen(
        title: String,
        navigationKey: String,
        init: @ScreenBuilderDsl ScreenBuilder.() -> Unit,
    ) : GuiScreen {
        val screenBuilder = ScreenBuilder(title, icon, false, navigationKey)
        screenBuilder.init()
        val screen = screenBuilder.build()
        screens.add(screen)
        guiBuilder.initScreen(title, icon, false, navigationKey, init)
        return screen
    }


    fun build(): GuiScreen = GuiScreen(
        GuiScreenInfo(title = title, icon = icon, primary = primary, navigationKey = navigationKey),
        floatingAction = {
            val scope = this
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
            ) {
                if (hasSetFloatingAction) scope.builderFloatingAction()
            }

        },
        content = {

            onGuiClosed { builderOnGuiClosed() }

            val leftFrameWidth by animateDpAsState(
                targetValue = if (isLeftFrameVisible()) 200.dp else 0.dp,
                animationSpec = tween(100)
            )

            val leftFrameOffset by animateOffsetAsState(
                targetValue = if (isLeftFrameVisible()) Offset.Zero else Offset(-200f, 0f),
                animationSpec = tween(100)
            )

            val rightFrameWidth by animateDpAsState(
                targetValue = if (isRightFrameVisible()) 200.dp else 0.dp,
                animationSpec = tween(100)
            )

            val rightFrameOffset by animateOffsetAsState(
                targetValue = if (isRightFrameVisible()) Offset.Zero else Offset(210f, 0f),
                animationSpec = tween(100)
            )


            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                ) {
                    if (hasSetLeftFrame) {
                        ScriptSurface(
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.height(if (primary) 567.5.dp else 547.5.dp)
                                    .width(leftFrameWidth)
                                    .offset(leftFrameOffset.x.dp, leftFrameOffset.y.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top,
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Box(modifier = Modifier.fillMaxHeight()
                                        .width(1.dp)
                                        .background(ScriptColorProvider.colors.blackOutlineColor)
                                    )
                                    extendableLeftFrameContent()
                                }

                            }


                        }
                    }
                    Column(
                        modifier = Modifier.height(if (primary) 567.5.dp else 547.5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                        ) {
                            mainFrame()
                        }
                    }

                }


                if (hasSetRightFrame) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Spacer(modifier = Modifier.height(160.dp))
                            ScriptSurface(
                                modifier = Modifier.height(200.dp)
                                    .width(rightFrameWidth)
                                    .offset(rightFrameOffset.x.dp, rightFrameOffset.y.dp)
                                    .thinOutline(),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    extendableRightFrameContent()
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

fun buildGui(
    title: String,
    icon: ScriptIcon,
    init: @GuiBuilderDsl GuiBuilder.() -> Unit,
): ScriptGui {
    val builder = GuiBuilder(
        title = title,
        icon = icon
    )
    builder.init()
    return builder.build()
}

fun buildScreen(
    title: String,
    icon: ScriptIcon,
    primary: Boolean = true,
    navigationKey: String = title,
    init: (@ScreenBuilderDsl ScreenBuilder).() -> Unit,
) : GuiScreen {
    val builder = ScreenBuilder(
        title = title,
        icon = icon,
        primary = primary,
        navigationKey = navigationKey
    )
    builder.init()
    return builder.build()
}
