package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.Mode
import com.sorrowblue.comicviewer.feature.settings.Settings
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import com.sorrowblue.comicviewer.framework.navigation.AppNavController
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton

@Serializable
@NavGraph(startDestination = Settings::class)
data object SettingsNavGraph {

    @DestinationInGraph<Settings>
    @DestinationInGraph<Authentication>
    object Include
}

@Singleton
internal class SettingsNavGraphNavigator(
    @Qualifier(AppNavController::class) private val navController: NavController,
) : SettingsScreenNavigator, AuthenticationScreenNavigator {

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
            navController.navigate(Authentication(Mode.Register))
        } else {
            navController.navigate(Authentication(Mode.Erase))
        }
    }

    override fun onPasswordChange() {
        navController.navigate(Authentication(Mode.Change))
    }
}
