package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenNavigator
import org.koin.core.annotation.Factory

@Factory
internal class SettingsNavGraphNavigator(
    private val navController: NavController,
) : SettingsScreenNavigator, TutorialScreenNavigator, AuthenticationScreenNavigator {

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onCompleted() {
        navController.popBackStack()
    }

    override fun onStartTutorialClick() {
        navController.navigate(Tutorial)
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

    override fun onCompleteTutorial() {
        navController.popBackStack()
    }
}
