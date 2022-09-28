package scripts.kt.gui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


/* Written by IvanEOD 9/28/2022, at 8:59 AM */


@Composable
fun ScriptScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScriptScaffoldState = rememberScriptScaffoldState(),
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    navigationRail: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    verticalScrollBar: @Composable () -> Unit = {},
    horizontalScrollBar: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (PaddingValues) -> Unit,
) {
    val child = @Composable { childModifier: Modifier ->
        Surface(modifier = childModifier, color = containerColor, contentColor = contentColor) {
            ScriptScaffoldLayout(
                topBar = topBar,
                bottomBar = bottomBar,
                navigationRail = navigationRail,
                content = content,
                snackbar = {
                    snackbarHost(scaffoldState.snackbarHostState)
                },
                fab = floatingActionButton,
                verticalScrollBar = verticalScrollBar,
                horizontalScrollBar = horizontalScrollBar
            )
        }
    }
    child(modifier)
}


/**
 * ------------------------------------------------------------------------------
 * Script Scaffold Layout Design
 * -----------------------------------------------------------------------------
 */
@Composable
private fun ScriptScaffoldLayout(
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    navigationRail: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    fab: @Composable () -> Unit,
    snackbar: @Composable () -> Unit,
    verticalScrollBar: @Composable() () -> Unit,
    horizontalScrollBar: @Composable() () -> Unit,
) {
    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val contentMargin = 0.dp.roundToPx()
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        layout(layoutWidth, layoutHeight) {

            val topBarComponents = subcompose(slotId = ScriptScaffoldLayoutContent.TopBar) {
                CompositionLocalProvider(content = topBar)
            }.map { it.measure(looseConstraints) }
            val topBarHeight = topBarComponents.maxByOrNull { it.height }?.height ?: 0

            val fabComponents = subcompose(ScriptScaffoldLayoutContent.Fab, fab).mapNotNull { measurable ->
                measurable.measure(looseConstraints).takeIf { it.height != 0 && it.width != 0 }
            }
            val fabPlacement = if (fabComponents.isNotEmpty()) {
                val fabWidth = fabComponents.maxByOrNull { it.width }!!.width
                val fabHeight = fabComponents.maxByOrNull { it.height }!!.height
                val fabLeftOffset =
                    if (layoutDirection == LayoutDirection.Ltr) layoutWidth - FabSpacing.roundToPx() - fabWidth
                    else FabSpacing.roundToPx()
                FabPlacement(left = fabLeftOffset, width = fabWidth, height = fabHeight)
            } else null

            val bottomBarComponents = subcompose(slotId = ScriptScaffoldLayoutContent.BottomBar) {
                CompositionLocalProvider(LocalFabPlacement provides fabPlacement, content = bottomBar)
            }.map { it.measure(looseConstraints) }
            val bottomBarHeight = bottomBarComponents.maxByOrNull { it.height }?.height ?: 0

            val navigationRailComponents =
                subcompose(slotId = ScriptScaffoldLayoutContent.NavigationRail, content = navigationRail)
                    .map { it.measure(looseConstraints) }
            val navigationRailWidth = navigationRailComponents.maxByOrNull { it.width }?.width ?: 0

            val horizontalScrollBarComponent = subcompose(slotId = ScriptScaffoldLayoutContent.HorizontalScrollbar,
                content = horizontalScrollBar).map { measurable ->
                measurable.measure(looseConstraints.copy(maxWidth = layoutWidth - navigationRailWidth))
            }
            val horizontalScrollBarHeight = horizontalScrollBarComponent.maxByOrNull { it.height }?.height ?: 0
            val horizontalScrollBarYOffset = layoutHeight - bottomBarHeight - horizontalScrollBarHeight

            val verticalScrollBarComponent = subcompose(slotId = ScriptScaffoldLayoutContent.VerticalScrollbar,
                content = verticalScrollBar).map { measurable ->
                measurable.measure(looseConstraints.copy(maxHeight = layoutHeight - topBarHeight - bottomBarHeight - horizontalScrollBarHeight))
            }
            val verticalScrollBarWidth = verticalScrollBarComponent.maxByOrNull { it.width }?.width ?: 0
            val verticalScrollBarXOffset = layoutWidth - verticalScrollBarWidth

            val fabOffsetFromBottom = fabPlacement?.let {
                bottomBarHeight + horizontalScrollBarHeight + it.height + FabSpacing.roundToPx()
            }

            val snackbarComponents = subcompose(ScriptScaffoldLayoutContent.Snackbar, snackbar).map {
                it.measure(looseConstraints.copy(maxWidth = (layoutWidth * 0.40).roundToInt()))
            }
            val snackbarHeight = snackbarComponents.maxByOrNull { it.height }?.height ?: 0
            val snackbarWidth = snackbarComponents.maxByOrNull { it.width }?.width ?: 0
            val snackbarOffsetFromBottom =
                if (snackbarHeight != 0) snackbarHeight + bottomBarHeight + horizontalScrollBarHeight
                else 0
            val snackbarOffsetFromLeft = (layoutWidth / 2) - (snackbarWidth / 2)

            val bodyContentHeight =
                layoutHeight - topBarHeight - bottomBarHeight - horizontalScrollBarHeight - (contentMargin * 2)
            val bodyContentWidth = layoutWidth - navigationRailWidth - verticalScrollBarWidth - (contentMargin * 2)
            val bodyContentComponent = subcompose(ScriptScaffoldLayoutContent.MainContent) {
                val innerPadding = PaddingValues(all = 12.dp)
                content(innerPadding)
            }.map { it.measure(looseConstraints.copy(maxHeight = bodyContentHeight, maxWidth = bodyContentWidth)) }

            bodyContentComponent.forEach {
                it.place(navigationRailWidth + contentMargin, topBarHeight + contentMargin)
            }
            topBarComponents.forEach { it.place(x = 0, y = 0) }
            navigationRailComponents.forEach { it.place(x = 0, y = topBarHeight) }
            bottomBarComponents.forEach { it.place(x = navigationRailWidth, y = layoutHeight - bottomBarHeight) }
            verticalScrollBarComponent.forEach { it.place(x = verticalScrollBarXOffset, y = topBarHeight) }
            horizontalScrollBarComponent.forEach { it.place(x = navigationRailWidth, y = horizontalScrollBarYOffset) }
            snackbarComponents.forEach { it.place(snackbarOffsetFromLeft, layoutHeight - snackbarOffsetFromBottom) }
            fabPlacement?.let { placement ->
                fabComponents.forEach { it.place(placement.left, layoutHeight - fabOffsetFromBottom!!) }
            }
        }

    }
}

/**
 * State for [Scaffold] composable component.
 * @param drawerState the drawer state
 * @param snackbarHostState the snackbar host state
 */
@Stable
data class ScriptScaffoldState(
    val drawerState: DrawerState,
    val snackbarHostState: SnackbarHostState,
)

/**
 * Creates a [ScaffoldState] with the default animation clock and memorizes it.
 * @param drawerState the drawer state
 * @param snackbarHostState the snackbar host state
 */
@Composable
fun rememberScriptScaffoldState(
    drawerState: DrawerState = DrawerState(DrawerValue.Closed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): ScriptScaffoldState {
    return remember {
        ScriptScaffoldState(
            drawerState = drawerState,
            snackbarHostState = snackbarHostState
        )
    }
}

/**
 * Placement information for a [FloatingActionButton] inside a [Scaffold].
 *
 * @property left the FAB's offset from the left edge of the bottom bar, already adjusted for RTL
 * support
 * @property width the width of the FAB
 * @property height the height of the FAB
 */
@Immutable
internal data class FabPlacement(
    val left: Int,
    val width: Int,
    val height: Int,
)

/**
 * CompositionLocal containing a [FabPlacement] that is used to calculate the FAB bottom offset.
 */
internal val LocalFabPlacement = staticCompositionLocalOf<FabPlacement?> { null }
private val FabSpacing = 16.dp

private enum class ScriptScaffoldLayoutContent {
    TopBar,
    MainContent,
    Fab,
    BottomBar,
    NavigationRail,
    Snackbar,
    VerticalScrollbar,
    HorizontalScrollbar
}