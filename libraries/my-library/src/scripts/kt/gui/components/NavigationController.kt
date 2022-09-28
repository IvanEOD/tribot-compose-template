package scripts.kt.gui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import org.tribot.script.sdk.Log


/* Written by IvanEOD 9/28/2022, at 9:20 AM */
class NavigationController(
    private val startDestination: GuiScreen,
    private val screens: MutableSet<GuiScreen>
) {
    private val allScreens = mutableSetOf<GuiScreen>()

    private fun addScreen(screen: GuiScreen) {
        allScreens.add(screen)
        screen.screens.forEach { addScreen(it) }
    }

    init {
        screens.forEach(::addScreen)
    }


    val backStack: SnapshotStateList<GuiScreen> = mutableStateListOf()
    var currentScreen by mutableStateOf(startDestination)

    fun navigate(navigationKey: String) {
        if (screens.none { it.info.navigationKey == navigationKey }) {
            Log.warn("Screen key [$navigationKey] not found, could not navigate to it")
            Log.trace("Existing keys: ${screens.joinToString(", ") { "${it.info.title} [${it.info.navigationKey}]" }}")
            return
        }
        screens.find { it.info.navigationKey == navigationKey }?.let { navigate(it) }
    }

    fun navigate(scr: GuiScreen) {
        val previous = currentScreen
        val next = scr

        if (next.info.primary) {
            backStack.clear()
        } else {
            if (previous != next) {
                if (backStack.contains(next)) {
                    val index = backStack.indexOf(next)
                    if (index != -1) {
                        backStack.removeRange(index, backStack.size)
                    }
                } else backStack.add(previous)
            }
        }
        currentScreen = next
        println(backStack.joinToString(", "))
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.last()
            backStack.remove(currentScreen)
        }
    }

}

@Composable
fun rememberNavigation(
    startDestination: GuiScreen,
    screens: MutableSet<GuiScreen>
): MutableState<NavigationController> {
    return rememberSaveable { mutableStateOf(NavigationController(startDestination, screens)) }
}

data class NavigationHost(
    val controller: NavigationController,
    val contents: @Composable NavigationGraphBuilder.() -> Unit
) {
    @Composable
    fun build() {
        NavigationGraphBuilder().render()
    }

    inner class NavigationGraphBuilder(
        val navigation: NavigationController = this@NavigationHost.controller,
    ) {
        @Composable
        fun render() {
            this@NavigationHost.contents(this)
        }
    }
}


@Composable
fun NavigationHost.NavigationGraphBuilder.composable(
    route: GuiScreen,
    content: @Composable (Modifier) -> Unit
) {
    if (navigation.currentScreen == route) {
        var timeout by remember { mutableStateOf(false) }
        val alpha by animateFloatAsState(
            targetValue = if (timeout) 1f else 0f,
            animationSpec = tween(500)
        )
        LaunchedEffect(Unit) {
            delay(100)
            timeout = true
        }
        content(Modifier.fillMaxSize().alpha(alpha))
    }
}