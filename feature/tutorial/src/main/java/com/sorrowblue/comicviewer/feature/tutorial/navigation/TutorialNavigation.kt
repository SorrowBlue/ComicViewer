package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreen
import com.sorrowblue.comicviewer.feature.tutorial.destinations.TutorialScreenDestination

fun NavGraphBuilder.tutorialScreen(onComplete: () -> Unit) {
    composable(TutorialScreenDestination) {
        TutorialScreen(onComplete = onComplete)
    }
}

fun NavController.navigateToTutorial(navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(TutorialScreenDestination, navOptionsBuilder = navOptionsBuilder)
}
