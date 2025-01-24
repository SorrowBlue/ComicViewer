package com.sorrowblue.comicviewer.framework.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.ComposeNavigatorDestinationBuilder
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.DialogNavigatorDestinationBuilder
import androidx.navigation.compose.navigation
import androidx.navigation.get
import kotlin.reflect.KClass
import kotlin.reflect.KType
import org.koin.compose.koinInject
import org.koin.compose.module.rememberKoinModules
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.Qualifier
import org.koin.core.module.Module
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

sealed interface DestinationStyle {

    data object Composable : DestinationStyle

    data object Dialog : DestinationStyle

    data object Auto : DestinationStyle

}

interface NavGraph {
    val startDestination: KClass<*>
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>

    val screenDestinations: List<ScreenDestination>
    val nestedNavGraphs: List<NavGraph>
}

interface ScreenDestination {
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>
    val style: DestinationStyle
    val navController: NavController
        @Composable
        get() = koinInject(qualifier<AppNavController>())

    @Composable
    fun NavBackStackEntry.Content()
}

@Qualifier
annotation class AppNavController

@Composable
fun NavGraphNavHost(
    navGraph: NavGraph,
    startDestination: KClass<*>? = null,
    navController: NavHostController,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = startDestination ?: navGraph.startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = navGraph.route
    ) {
        navGraph.nestedNavGraphs.forEach {
            navGraphNavigation(it, isCompact)
        }
        navGraph.screenDestinations.forEach {
            screenDestination(it, isCompact)
        }
    }
}

fun NavGraphBuilder.navGraphNavigation(
    navGraph: NavGraph,
    isCompact: Boolean,
) {
    navigation(
        startDestination = navGraph.startDestination,
        route = navGraph.route,
        typeMap = navGraph.typeMap,
    ) {
        navGraph.nestedNavGraphs.forEach {
            navGraphNavigation(it, isCompact)
        }
        navGraph.screenDestinations.forEach {
            screenDestination(it, isCompact)
        }
    }
}

fun NavGraphBuilder.screenDestination(
    screenDestination: ScreenDestination,
    isCompact: Boolean,
) {
    when (screenDestination.style) {
        DestinationStyle.Composable ->
            addComposable(screenDestination)

        DestinationStyle.Dialog ->
            addDialog(screenDestination = screenDestination)

        DestinationStyle.Auto -> {
            if (isCompact) {
                addComposable(screenDestination)
            } else {
                addDialog(screenDestination = screenDestination)
            }
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
private fun NavGraphBuilder.addComposable(screenDestination: ScreenDestination) {
    destination(
        ComposeNavigatorDestinationBuilder(
            provider[ComposeNavigator::class],
            screenDestination.route,
            screenDestination.typeMap
        ) {
            rememberKoinModules { listOf(module { single { screenDestination.typeMap } }) }
            with(screenDestination) {
                it.Content()
            }
        }
    )
}

@OptIn(KoinExperimentalAPI::class)
private fun NavGraphBuilder.addDialog(screenDestination: ScreenDestination) {
    destination(
        DialogNavigatorDestinationBuilder(
            navigator = provider[DialogNavigator::class],
            route = screenDestination.route,
            typeMap = screenDestination.typeMap,
            dialogProperties = DialogProperties(),
        ) {
            rememberKoinModules { listOf(module { single { screenDestination.typeMap } }) }
            with(screenDestination) {
                it.Content()
            }
        }
    )
}
