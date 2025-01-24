package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import org.koin.compose.getKoin
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module

var lastKnownNavGraphRoute: String? = null

@OptIn(KoinInternalApi::class)
@Composable
inline fun rememberKoinNavGraphModules(
    navController: NavController,
    navGraphRoute: String,
    crossinline modules: @DisallowComposableCalls () -> List<Module>,
): CompositionKoinModuleUnLoader {
    val koin = getKoin()
    val currentNavGraphRoute = navController.currentDestination?.parent?.route
    if (lastKnownNavGraphRoute == null || lastKnownNavGraphRoute != currentNavGraphRoute) {
        lastKnownNavGraphRoute = currentNavGraphRoute
        koin.logger.debug("load modules")
        loadKoinModules(modules())
    }
    return remember {
        CompositionKoinModuleUnLoader(
            modules = modules(),
            koin = koin,
            navGraphRoute = navGraphRoute,
            navController = navController
        )
    }
}

class CompositionKoinModuleUnLoader(
    private val modules: List<Module>,
    private val koin: Koin,
    private val navGraphRoute: String,
    private val navController: NavController,
) : RememberObserver {

    override fun onRemembered() {
        // Nothing to do
    }

    override fun onForgotten() {
        unloadModules()
    }

    override fun onAbandoned() {
        // Nothing to do
    }

    @OptIn(KoinInternalApi::class)
    private fun unloadModules() {
        val currentRoute = navController.currentDestination?.parent?.route
        if (currentRoute != navGraphRoute) {
            koin.logger.debug("$this -> unload modules")
            lastKnownNavGraphRoute = currentRoute
            koin.unloadModules(modules)
        }
    }
}
