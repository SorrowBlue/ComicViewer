package com.sorrowblue.comicviewer.framework.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.ComposeNavigatorDestinationBuilder
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.DialogNavigatorDestinationBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.get
import kotlin.reflect.KClass
import org.koin.compose.module.rememberKoinModules
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module

@Composable
fun NavGraphNavHost(
    navGraph: NavGraph,
    startDestination: KClass<*>? = null,
    navController: NavHostController,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    val navTransition = remember { navGraph.transition }
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = startDestination ?: navGraph.startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = navGraph.route,
        enterTransition = { with(navTransition) { enterTransition() } },
        exitTransition = { with(navTransition) { exitTransition() } },
        popEnterTransition = { with(navTransition) { popEnterTransition() } },
        popExitTransition = { with(navTransition) { popExitTransition() } },
        sizeTransform = { with(navTransition) { sizeTransform() } },
    ) {
        navGraph.nestedNavGraphs.forEach {
            navGraphNavigation(navGraph = it, navController = navController, isCompact = isCompact)
        }
        navGraph.screenDestinations.forEach {
            screenDestination(
                screenDestination = it,
                navController = navController,
                isCompact = isCompact,
                navTransition = navTransition
            )
        }
    }
}

fun NavGraphBuilder.navGraphNavigation(
    navGraph: NavGraph,
    navController: NavController,
    isCompact: Boolean,
) {
    val navTransition = navGraph.transition
    navigation(
        startDestination = navGraph.startDestination,
        route = navGraph.route,
        typeMap = navGraph.typeMap,
        enterTransition = { with(navTransition) { enterTransition() } },
        exitTransition = { with(navTransition) { exitTransition() } },
        popEnterTransition = { with(navTransition) { popEnterTransition() } },
        popExitTransition = { with(navTransition) { popExitTransition() } },
        sizeTransform = { with(navTransition) { sizeTransform() } },
    ) {
        navGraph.nestedNavGraphs.forEach {
            navGraphNavigation(navGraph = it, navController = navController, isCompact = isCompact)
        }
        navGraph.screenDestinations.forEach {
            screenDestination(
                screenDestination = it,
                navController = navController,
                isCompact = isCompact,
                navTransition = navTransition
            )
        }
    }
}

private fun NavGraphBuilder.screenDestination(
    screenDestination: ScreenDestination,
    navController: NavController,
    isCompact: Boolean,
    navTransition: NavTransition,
) {
    when (screenDestination.style) {
        DestinationStyle.Composable ->
            addComposable(
                screenDestination = screenDestination,
                navController = navController,
                navTransition = navTransition
            )

        DestinationStyle.Dialog ->
            addDialog(screenDestination = screenDestination, navController = navController)

        DestinationStyle.Auto -> {
            if (isCompact) {
                addComposable(
                    screenDestination = screenDestination,
                    navController = navController,
                    navTransition = navTransition
                )
            } else {
                addDialog(screenDestination = screenDestination, navController = navController)
            }
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
private fun NavGraphBuilder.addComposable(
    screenDestination: ScreenDestination,
    navController: NavController,
    navTransition: NavTransition,
) {
    destination(
        ComposeNavigatorDestinationBuilder(
            provider[ComposeNavigator::class],
            screenDestination.route,
            screenDestination.typeMap,
        ) {
            rememberKoinModules { listOf(module { single { screenDestination.typeMap } }) }
            with(screenDestination) {
                it.Content(navController = navController)
            }
        }.apply {
            this.enterTransition = { with(navTransition) { enterTransition() } }
            this.exitTransition = { with(navTransition) { exitTransition() } }
            this.popEnterTransition = { with(navTransition) { popEnterTransition() } }
            this.popExitTransition = { with(navTransition) { popExitTransition() } }
            this.sizeTransform = { with(navTransition) { sizeTransform() } }
        }
    )
}

@OptIn(KoinExperimentalAPI::class)
private fun NavGraphBuilder.addDialog(
    screenDestination: ScreenDestination,
    navController: NavController,
) {
    destination(
        DialogNavigatorDestinationBuilder(
            navigator = provider[DialogNavigator::class],
            route = screenDestination.route,
            typeMap = screenDestination.typeMap,
            dialogProperties = DialogProperties(),
        ) {
            rememberKoinModules { listOf(module { single { screenDestination.typeMap } }) }
            with(screenDestination) {
                it.Content(navController = navController)
            }
        }
    )
}
