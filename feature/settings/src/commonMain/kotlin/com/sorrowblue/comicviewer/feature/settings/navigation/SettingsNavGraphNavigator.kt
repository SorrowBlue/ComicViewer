package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenNavigator
import org.koin.core.annotation.Factory

@Factory
internal class SettingsNavGraphNavigator(
    private val navController: NavController,
) : SettingsScreenNavigator, TutorialScreenNavigator {

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onStartTutorialClick() {
        navController.navigate(Tutorial)
    }

    override fun onCompleteTutorial() {
        navController.popBackStack()
    }
}
