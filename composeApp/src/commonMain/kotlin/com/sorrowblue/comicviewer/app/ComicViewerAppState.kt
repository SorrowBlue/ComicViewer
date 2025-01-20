package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffoldUiState
import com.sorrowblue.comicviewer.app.component.MainScreenTab
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import logcat.logcat

internal sealed interface ComicViewerAppEvent {

    data class Navigate(
        val route: Any,
        val navOptions: NavOptions? = null,
    ) : ComicViewerAppEvent
}

@Composable
internal fun rememberComicViewerAppState(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): ComicViewerAppState {
    return remember {
        ComicViewerAppStateImpl(
            lifecycle = lifecycle,
            scope = scope,
            navController = navController,
        )
    }
}

internal interface ComicViewerAppState {

    val navController: NavHostController
    val uiState: ComicViewerScaffoldUiState
    val events: EventFlow<ComicViewerAppEvent>

    fun onTabSelect(tab: MainScreenTab)
}

private class ComicViewerAppStateImpl(
    lifecycle: Lifecycle,
    private val scope: CoroutineScope,
    override val navController: NavHostController,
) :
    ComicViewerAppState {

    override var uiState: ComicViewerScaffoldUiState by mutableStateOf(ComicViewerScaffoldUiState())
        private set

    override val events = EventFlow<ComicViewerAppEvent>()


    init {
        navController.currentBackStackEntryFlow.onEach { backStackEntry ->
            val hierarchy = backStackEntry.destination.hierarchy
            logcat {
                "destination.hierarchy=${
                    hierarchy.joinToString(",") {
                        it.route?.split('/')?.firstOrNull().orEmpty().ifEmpty { "null" }
                    }
                }"
            }
            val currentTab = MainScreenTab.entries.find { tab ->
                hierarchy.any { it.hasRoute(tab.navGraph::class) }
            }
            uiState = uiState.copy(currentTab = currentTab)
        }.flowWithLifecycle(lifecycle)
            .launchIn(scope)
    }

    override fun onTabSelect(tab: MainScreenTab) {
        val navGraph = tab.navGraph
        logcat { "Selected navGraph = $navGraph" }
        if (navController.currentBackStackEntry?.destination?.hierarchy?.any { it.hasRoute(navGraph::class) } == true) {
//            navTabHandler.click.tryEmit(Unit)
        } else {
            events.tryEmit(
                ComicViewerAppEvent.Navigate(
                    navGraph,
                    navOptions {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                )
            )
        }
    }
}
