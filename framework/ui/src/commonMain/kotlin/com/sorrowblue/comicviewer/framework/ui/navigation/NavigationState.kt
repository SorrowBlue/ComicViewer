package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.PolymorphicSerializer

/**
 * Create a navigation state that persists config changes and process death.
 *
 * @param startRoute the start route. The user will exit the app through this route
 * @param topLevelRoutes the top level routes. The user will exit the app through one of these routes
 * @param configuration the saved state configuration
 * @return [NavigationState] that persists config changes and process death
 */
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavigationKey>,
    configuration: SavedStateConfiguration,
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        stateSerializer = PolymorphicSerializer(NavKey::class),
        configuration = configuration,
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks: Map<NavKey, NavBackStack<NavKey>> =
        topLevelRoutes.associateWith { key -> rememberNavBackStack(configuration, key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            topLevelRoutes = topLevelRoutes,
            backStacks = backStacks,
        )
    }
}

/**
 * State holder for navigation state.
 *
 * @param startRoute - the start route. The user will exit the app through this route.
 * @param topLevelRoute - the current top level route
 * @param backStacks - the back stacks for each top level route
 */
class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val topLevelRoutes: Set<NavigationKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var topLevelRoute: NavKey by topLevelRoute

    /**
     * Convert the navigation state into `NavEntry`s that have been decorated with a
     * `SaveableStateHolder`.
     *
     * @param entryProvider - the entry provider used to convert the keys in the
     * back stacks to `NavEntry`s.
     */
    @Composable
    fun toDecoratedEntries(
        entryDecorators: List<NavEntryDecorator<NavKey>> = listOf(),
        entryProvider: (NavKey) -> NavEntry<NavKey>,
    ): List<NavEntry<NavKey>> {
        // For each back stack, create a `SaveableStateHolder` decorator and use it to decorate
        // the entries from that stack. When backStacks changes, `rememberDecoratedNavEntries` will
        // be recomposed and a new list of decorated entries is returned.
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = entryDecorators,
                entryProvider = entryProvider,
            )
        }

        // Only return the entries for the stacks that are currently in use.
        return getTopLevelRoutesInUse().flatMap { decoratedEntries[it].orEmpty() }
    }

    /**
     * Get the top level routes that are currently in use. The start route is always the first route
     * in the list. This means the user will always exit the app through the starting route
     * ("exit through home" pattern). The list will contain a maximum of one other route. This is a
     * design decision. In your app, you may wish to allow more than two top level routes to be
     * active.
     *
     * Note that even if a top level route is not in use its state is still retained.
     *
     * @return the current top level routes that are in use.
     */
    private fun getTopLevelRoutesInUse(): List<NavKey> = if (topLevelRoute == startRoute) {
        listOf(startRoute)
    } else {
        listOf(startRoute, topLevelRoute)
    }
}
