package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.tutorial.TutorialRoute

const val TutorialRoute = "tutorial"

fun NavGraphBuilder.tutorialScreen(onComplete: () -> Unit) {
    composable(TutorialRoute) {
        TutorialRoute(onComplete = onComplete)
    }
}

fun NavController.navigateToTutorial(navOptions: NavOptions? = null) {
    navigate(TutorialRoute, navOptions)
}
