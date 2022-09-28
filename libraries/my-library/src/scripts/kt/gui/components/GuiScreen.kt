package scripts.kt.gui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import scripts.kt.gui.HomeScreen
import scripts.kt.gui.components.alert.ScriptGuiAlertState


/* Written by IvanEOD 9/28/2022, at 9:20 AM */
@Stable
@Immutable
class GuiScreen(
    val info: GuiScreenInfo,
    val floatingAction: @Composable GuiScreenScope.() -> Unit = { },
    internal val content: @Composable GuiScreenScope.() -> Unit = { },
) : GuiScreenScope {
    override var leftFrameVisible by mutableStateOf(false)
    override var rightFrameVisible by mutableStateOf(false)
    override var guiScope: GuiScope by mutableStateOf(tempGuiScope)
    override val screens = info.screens

    var onGuiClosed by mutableStateOf<() -> Unit>({})

    override fun onGuiClosed(block: () -> Unit) {
        onGuiClosed = block
    }

    override fun toString() = info.title

}

@Composable
fun GuiScreen.render(
    guiScope: GuiScope,
) {
    this.guiScope = guiScope

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = { content() }
    )

}

@OptIn(DelicateCoroutinesApi::class)
private val tempGuiScope = object : GuiScope {
    override var scope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext)
    override var state: GuiState = GuiState.Running
    override var scrollbarState: ScriptScaffoldScrollbarState = ScriptScaffoldScrollbarState(
        LazyListState(),
        ScrollState(0),
        ScrollbarAdapter(LazyListState()),
        ScrollbarAdapter(ScrollState(0))
    )
    override var navigation: NavigationController = NavigationController(HomeScreen, mutableSetOf())
    override var desktopScaffoldState: ScriptScaffoldState = ScriptScaffoldState(
        DrawerState(DrawerValue.Open) { _ -> true },
        SnackbarHostState()
    )
    override var screens: List<GuiScreen> = emptyList()
    override var currentScreen: GuiScreen = HomeScreen
    override var snackbarResult: SnackbarResult? = null
    override var alertState: ScriptGuiAlertState = ScriptGuiAlertState()
    override var onAlertConfirmed: MutableState<() -> Unit> = mutableStateOf({})
    override var onAlertCancelled: MutableState<() -> Unit> = mutableStateOf({})

}


@Stable
@Immutable
data class GuiScreenInfo(
    val title: String,
    val icon: ScriptIcon,
    val primary: Boolean = true,
    val navigationKey: String = title,
    val screens: List<GuiScreen> = emptyList(),
)

@Stable
@Immutable
interface GuiScreenScope {

    var leftFrameVisible: Boolean
    var rightFrameVisible: Boolean

    var guiScope: GuiScope
    val screens: List<GuiScreen>

    fun onGuiClosed(block: () -> Unit)

    fun isLeftFrameVisible() = leftFrameVisible
    fun isRightFrameVisible() = rightFrameVisible
    fun showLeftFrame() {
        leftFrameVisible = true
    }

    fun showRightFrame() {
        rightFrameVisible = true
    }

    fun hideLeftFrame() {
        leftFrameVisible = false
    }

    fun hideRightFrame() {
        rightFrameVisible = false
    }

    fun toggleLeftFrame() = if (isLeftFrameVisible()) hideLeftFrame() else showLeftFrame()
    fun toggleRightFrame() = if (isRightFrameVisible()) hideRightFrame() else showRightFrame()

}