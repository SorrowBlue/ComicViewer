package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import logcat.logcat

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

    val backStack get() = state.backStacks.getValue(state.topLevelRoute)

    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            logcat { "#navigate: ${backStack.lastOrNull()} > $route" }
            // This is a top level route, just switch to it
            state.topLevelRoute = route
        } else if (state.backStacks[state.topLevelRoute]?.lastOrNull() != route) {
            logcat { "#navigate: ${backStack.lastOrNull()} > $route" }
            state.backStacks[state.topLevelRoute]?.add(route)
        } else {
            logcat { "#navigate: skipped $route" }
        }
    }

    inline fun <reified T : NavKey> popNavigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            logcat { "#popNavigate: ${backStack.lastOrNull()} > $route" }
            // This is a top level route, just switch to it
            state.topLevelRoute = route
        } else {
            val currentStack = state.backStacks[state.topLevelRoute]
                ?: error("Stack for ${state.topLevelRoute} not found")
            val fromIndex = currentStack.indexOfLast { it is T }
            if (0 <= fromIndex) {
                if (fromIndex < currentStack.size) {
                    logcat { "#popNavigate: pop: ${currentStack.lastOrNull()} -> ${currentStack[fromIndex - 1]}" }
                    currentStack.subList(fromIndex, currentStack.size).clear()
                } else {
                    logcat { "#popNavigate: Could not pop ${T::class.simpleName}. It is top of backstack." }
                }
            } else {
                logcat { "#popNavigate: Not found route ${T::class.simpleName}" }
            }
            logcat { "#popNavigate: navigate: ${currentStack.lastOrNull()} -> $route" }
            currentStack.add(route)
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            logcat { "#goBack ${currentStack.lastOrNull()} > ${state.startRoute}" }
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull().let {
                logcat { "#goBack $it > ${currentStack.lastOrNull()}" }
            }
        }
    }

    inline fun <reified T : NavKey> pop(inclusive: Boolean) {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val index = currentStack.indexOfLast { it is T }
        if (0 <= index) {
            val fromIndex = if (inclusive) index else index + 1
            if (fromIndex < currentStack.size) {
                logcat { "#pop: inclusive=$inclusive, ${currentStack.lastOrNull()} -> ${currentStack[fromIndex - 1]}" }
                currentStack.subList(fromIndex, currentStack.size).clear()
            } else {
                logcat { "#pop: Could not pop ${T::class.simpleName}. It is top of backstack." }
            }
        } else {
            logcat { "#pop: Not found route ${T::class.simpleName}" }
        }
    }
}
