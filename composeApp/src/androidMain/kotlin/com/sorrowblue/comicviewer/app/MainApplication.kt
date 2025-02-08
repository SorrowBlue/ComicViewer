package com.sorrowblue.comicviewer.app

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.ComposeNavigatorDestinationBuilder
import androidx.navigation.get
import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import com.sorrowblue.comicviewer.framework.navigation.NavTransition
import com.sorrowblue.comicviewer.framework.navigation.ScreenDestination
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.compose.module.rememberKoinModules
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

@KoinExperimentalAPI
internal class MainApplication : SplitCompatApplication(), KoinStartup {

    override fun onKoinStartup() = KoinConfiguration {
        logcat(LogPriority.INFO) { "onKoinStartup" }
        androidLogger()
        androidContext(this@MainApplication)
        modules(DiModule().module)
        modules(SettingsModule().module)
        defaultModule()
        workManagerFactory()
    }

    override fun onCreate() {
        super.onCreate()
        logcat(LogPriority.INFO) { "onCreate" }
    }
}

@OptIn(KoinExperimentalAPI::class)
private fun NavGraphBuilder.addComposable(
    screenDestination: ScreenDestination,
    navController: NavController,
    navTransition: NavTransition,
) {
    provider.getNavigator(ComposeNavigator::class.java)
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
