package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No AdaptiveNavigationSuiteState provided")
}

@Composable
fun rememberNavigator(
    startKey: NavKey,
    topLevelRoutes: Set<NavigationKey>,
    configuration: SavedStateConfiguration,
): Navigator {
    val state = rememberNavigationState(
        startRoute = startKey,
        topLevelRoutes = topLevelRoutes,
        configuration = configuration,
    )
    return remember { Navigator(state) }
}

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState) {
    val topLevelKey: NavKey get() = state.topLevelRoute
    val topLevelRoutes get() = state.topLevelRoutes

    val backStack get() = state.stacksInUse

    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            // This is a top level route, just switch to it
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    inline fun <reified T : NavKey> pop(inclusive: Boolean) {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val index = currentStack.indexOfLast { it is T }
        if (0 <= index) {
            repeat(currentStack.lastIndex - (if (inclusive) index - 1 else index)) {
                currentStack.removeLastOrNull()
            }
        }
    }
}
