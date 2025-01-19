package com.sorrowblue.comicviewer.framework.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.koin.compose.scope.KoinScope
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

sealed interface DestinationStyle {

    object Composable : DestinationStyle

    object Dialog : DestinationStyle

}

interface NavGraph {
    val startDestination: KClass<*>
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>

    val screenDestinations: List<ScreenDestination<*>>
    val nestedNavGraphs: List<NavGraph>
}

interface ScreenDestination<T : Any> {
    val route: KClass<T>
    val typeMap: Map<KType, NavType<*>>
    val style: DestinationStyle
    val navController: NavController
        @Composable
        get() = LocalNavController.current

    @Composable
    fun NavBackStackEntry.Content()
}

fun navigationModule(navController: NavHostController): Module {
    return module { single<NavController> { navController } }
}
@Composable
inline fun <reified T : NavGraph> NavGraphNavHost(
    navGraph: NavGraph,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    rememberKoinModules(unloadModules = true) {
        listOf(navigationModule(navController))
    }
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = navGraph.startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        navGraph.nestedNavGraphs.forEach {
            navGraphNavigation(it)
        }
        navGraph.screenDestinations.forEach {
            screenDestination(it)
        }
    }
}

fun NavGraphBuilder.navGraphNavigation(navGraph: NavGraph) {
    navigation(
        startDestination = navGraph.startDestination,
        route = navGraph.route,
        typeMap = navGraph.typeMap,
    ) {
        navGraph.nestedNavGraphs.forEach {
            navGraphNavigation(it)
        }
        navGraph.screenDestinations.forEach {
            screenDestination(it)
        }
    }
}

fun NavGraphBuilder.screenDestination(
    screenDestination: ScreenDestination<*>,
) {
    when (screenDestination.style) {
        DestinationStyle.Composable ->
            destination(
                ComposeNavigatorDestinationBuilder(
                    provider[ComposeNavigator::class],
                    screenDestination.route,
                    screenDestination.typeMap,
                    {
                        KoinScope<Unit>(screenDestination::class.qualifiedName!!) {
                            with(screenDestination) {
                                it.Content()
                            }
                        }

                    }
                )
            )

        DestinationStyle.Dialog ->
            destination(
                DialogNavigatorDestinationBuilder(
                    provider[DialogNavigator::class],
                    screenDestination.route::class,
                    typeMap = screenDestination.typeMap,
                    dialogProperties = DialogProperties(),
                ) {
                    KoinScope<Unit>(screenDestination::class.qualifiedName!!) {
                        with(screenDestination) {
                            it.Content()
                        }
                    }
                }
            )
    }
}
