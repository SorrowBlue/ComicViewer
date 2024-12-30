package com.sorrowblue.comicviewer.app

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.navigation
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCalls
import com.ramcosta.composedestinations.manualcomposablecalls.allDeepLinks
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.NavHostEngine
import com.ramcosta.composedestinations.spec.TypedDestinationSpec
import com.sorrowblue.comicviewer.framework.ui.AnimatedOrDialog
import com.sorrowblue.comicviewer.framework.ui.adaptive.isCompactWindowClass

/**
 * Remember custom nav host engine
 *
 * @param navHostContentAlignment
 * @return
 */
@Composable
internal fun rememberCustomNavHostEngine(
    navHostContentAlignment: Alignment = Alignment.Center,
): NavHostEngine {
    val isCompact = isCompactWindowClass()
    return remember(navHostContentAlignment, isCompact) {
        CustomNavHostEngine(
            isCompact = isCompact,
            navHostContentAlignment = navHostContentAlignment
        )
    }
}

/**
 * Custom nav host engine
 *
 * @property isCompact
 * @property navHostContentAlignment
 * @constructor Create empty Custom nav host engine
 */
internal class CustomNavHostEngine(
    val isCompact: Boolean,
    private val navHostContentAlignment: Alignment,
) : NavHostEngine {

    override val type = NavHostEngine.Type.DEFAULT

    @Composable
    override fun rememberNavController(
        vararg navigators: Navigator<out NavDestination>,
    ) = androidx.navigation.compose.rememberNavController(*navigators)

    @Composable
    override fun NavHost(
        modifier: Modifier,
        route: String,
        start: Direction,
        defaultTransitions: NavHostAnimatedDestinationStyle,
        navController: NavHostController,
        builder: NavGraphBuilder.() -> Unit,
    ) = with(defaultTransitions) {
        androidx.navigation.compose.NavHost(
            navController = navController,
            startDestination = start.route,
            modifier = modifier,
            route = route,
            contentAlignment = navHostContentAlignment,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
            sizeTransform = sizeTransform,
            builder = builder
        )
    }

    @SuppressLint("RestrictedApi")
    override fun NavGraphBuilder.navigation(
        navGraph: NavGraphSpec,
        manualComposableCalls: ManualComposableCalls,
        builder: NavGraphBuilder.() -> Unit,
    ) {
        val transitions = manualComposableCalls.manualAnimation(navGraph.route)
            ?: navGraph.defaultTransitions

        if (transitions != null) {
            with(transitions) {
                navigation(
                    startDestination = navGraph.startRoute.route,
                    route = navGraph.route,
                    arguments = navGraph.arguments,
                    deepLinks = navGraph.allDeepLinks(manualComposableCalls),
                    enterTransition = enterTransition,
                    exitTransition = exitTransition,
                    popEnterTransition = popEnterTransition,
                    popExitTransition = popExitTransition,
                    sizeTransform = sizeTransform,
                    builder = builder,
                )
            }
        } else {
            navigation(
                startDestination = navGraph.startRoute.route,
                route = navGraph.route,
                arguments = navGraph.arguments,
                deepLinks = navGraph.allDeepLinks(manualComposableCalls),
                builder = builder
            )
        }
    }

    @SuppressLint("RestrictedApi")
    override fun <T> NavGraphBuilder.composable(
        destination: TypedDestinationSpec<T>,
        navController: NavHostController,
        dependenciesContainerBuilder: @Composable DependenciesContainerBuilder<*>.() -> Unit,
        manualComposableCalls: ManualComposableCalls,
    ) = with(manualComposableCalls.manualAnimation(destination.route) ?: destination.style) {
        when (this) {
            is AnimatedOrDialog -> {
                if (isCompact) {
                    addComposable(
                        destination,
                        navController,
                        dependenciesContainerBuilder,
                        manualComposableCalls
                    )
                } else {
                    addDialog(
                        destination,
                        navController,
                        dependenciesContainerBuilder,
                        manualComposableCalls
                    )
                }
            }

            else -> addComposable(
                destination,
                navController,
                dependenciesContainerBuilder,
                manualComposableCalls
            )
        }
    }
}
