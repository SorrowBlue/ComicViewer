package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.settings.Settings
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

@Serializable
@NavGraph(startDestination = Settings::class)
data object SettingsNavGraph {

    @DestinationInGraph<Settings>
    @DestinationInGraph<Authentication>
    object Include
}

@Singleton
internal class SettingsNavGraphNavigator(private val navController: NavController) :
    SettingsScreenNavigator, AuthenticationScreenNavigator {

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onCompleted() {
        navController.popBackStack()
    }

    override fun onStartTutorialClick() {
        navController.navigate(TutorialNavGraph)
    }

    override fun navigateToChangeAuth(enabled: Boolean) {
        if (enabled) {
            navController.navigate(Authentication(ScreenType.Register))
        } else {
            navController.navigate(Authentication(ScreenType.Erase))
        }
    }

    override fun onPasswordChange() {
        navController.navigate(Authentication(ScreenType.Change))
    }
}
