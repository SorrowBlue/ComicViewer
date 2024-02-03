package com.sorrowblue.comicviewer.feature.tutorial.navigation

import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenNavigator
import com.sorrowblue.comicviewer.feature.tutorial.destinations.TutorialScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object TutorialNavGraph : AnimatedNavGraphSpec {
    override val route = "tutorial_graph"
    override val startRoute = TutorialScreenDestination
    override val destinationsByRoute = listOf(
        TutorialScreenDestination
    ).associateBy(DestinationSpec<*>::route)
    override val transitions = listOf(
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.SharedAxisY
        )
    )
}

fun DependenciesContainerBuilder<*>.dependencyTutorialNavGraph(onComplete: () -> Unit) {
    dependency(TutorialNavGraph) {
        object : TutorialScreenNavigator {
            override fun onComplete() = onComplete()
        }
    }
}
