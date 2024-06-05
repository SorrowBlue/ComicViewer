package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.feature.tutorial.NavGraphs
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenNavigator

@Composable
fun DependenciesContainerBuilder<*>.TutorialGraphDependencies(onComplete: () -> Unit) {
    navGraph(NavGraphs.tutorial) {
        dependency(TutorialScreenNavigator(onComplete))
    }
}
