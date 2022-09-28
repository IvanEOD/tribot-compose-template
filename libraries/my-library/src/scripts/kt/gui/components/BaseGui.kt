package scripts.kt.gui.components

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import org.tribot.script.sdk.ScriptListening
import scripts.kt.gui.HomeScreen
import scripts.kt.gui.ScriptGui
import scripts.kt.gui.components.alert.ScriptGuiAlertState


/* Written by IvanEOD 9/28/2022, at 10:28 AM */

fun ScriptGui(
    guiData: ScriptGui,
): GuiState {

    application(exitProcessOnExit = false) {

        val guiScope = rememberGuiScope(guiData)

        DisposableEffect(Unit) {
            val quitListener = Runnable {
                guiScope.state = GuiState.Cancelled
                exitApplication()
            }
            ScriptListening.addEndingListener(quitListener)
            onDispose {
                ScriptListening.removeEndingListener(quitListener)
            }
        }

        GuiWindow(
            title = guiData.title,
            guiScope = guiScope,
            icon = guiData.icon,
            onCloseRequest = {
                guiData.onGuiClosed()
                guiData.screens.forEach { screen ->
                    screen.onGuiClosed()
                    screen.screens.forEach { it.onGuiClosed() }
                }
                guiData.state = GuiState.Cancelled
                exitApplication()
            },
        )

    }
    return guiData.state
}


@Composable
fun rememberGuiScope(
    guiData: ScriptGui,
): GuiScope {
    val scaffoldState = rememberScriptScaffoldState()
    val scope = rememberCoroutineScope()
    val scrollbarState = rememberScriptScaffoldScrollbarState()


    val navgation by rememberNavigation(HomeScreen, screens = guiData.screens.toMutableSet())
    val currentScreen by remember {
        derivedStateOf {
            navgation.currentScreen
        }
    }

    return rememberSaveable(guiData) {
        object : GuiScope {
            override var scope: CoroutineScope = scope
            override var state: GuiState = guiData.state
            override var scrollbarState: ScriptScaffoldScrollbarState = scrollbarState
            override var navigation: NavigationController = navgation
            override var desktopScaffoldState: ScriptScaffoldState = scaffoldState
            override var screens: List<GuiScreen> = guiData.screens
            override var currentScreen: GuiScreen = currentScreen
            override var snackbarResult: SnackbarResult? by mutableStateOf(null)
            override var alertState: ScriptGuiAlertState = ScriptGuiAlertState()
            override var onAlertConfirmed: MutableState<() -> Unit> = mutableStateOf({ })
            override var onAlertCancelled: MutableState<() -> Unit> = mutableStateOf({ })
        }
    }
}

@Stable
@Immutable
interface GuiScope {
    var scope: CoroutineScope
    var state: GuiState
    var scrollbarState: ScriptScaffoldScrollbarState
    var navigation: NavigationController
    var desktopScaffoldState: ScriptScaffoldState
    var screens: List<GuiScreen>
    var currentScreen: GuiScreen
    var snackbarResult: SnackbarResult?
    var alertState: ScriptGuiAlertState
    var onAlertConfirmed: MutableState<() -> Unit>
    var onAlertCancelled: MutableState<() -> Unit>

    fun navigateTo(screen: String) = navigation.navigate(screen)
    fun navigateTo(screen: GuiScreen) = navigation.navigate(screen)

    suspend fun dispatchSnackbar(
        message: String,
        action: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: () -> Unit = {},
    ): SnackbarResult = desktopScaffoldState.snackbarHostState.showSnackbar(message, action, duration)

    fun dispatchAlert(
        title: String,
        message: String,
        confirmButtonText: String = "Confirm",
        cancelButtonText: String = "Cancel",
        onConfirm: () -> Unit = {},
        onCancel: () -> Unit = {},
    ) {
        alertState.title.value = title
        alertState.message.value = message
        alertState.confirmButtonText.value = confirmButtonText
        alertState.cancelButtonText.value = cancelButtonText
        onAlertConfirmed.value = onConfirm
        onAlertCancelled.value = onCancel
        alertState.showing.value = true
    }

    fun closeGui() {

    }

    fun alertCancelled() {
        alertState.result.value = false
        onAlertCancelled.value()
        alertState.showing.value = false
    }

    fun alertConfirmed() {
        alertState.result.value = true
        onAlertConfirmed.value()
        alertState.showing.value = false
    }


    fun toggleCurrentScreenLeftFrame() {
        currentScreen.toggleLeftFrame()
    }

    fun toggleCurrentScreenRightFrame() {
        currentScreen.toggleRightFrame()
    }


}

fun <T> T.toMutableState() = mutableStateOf(this)


@Stable
sealed class GuiState {
    object Running : GuiState()
    object Completed : GuiState()
    object Cancelled : GuiState()
    data class Error(val error: String) : GuiState()
}

