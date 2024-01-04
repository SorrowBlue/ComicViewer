package com.sorrowblue.comicviewer.feature.tutorial.navigation

import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
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

fun DestinationScopeWithNoDependencies<*>.tutorialNavGraphNavigator(navigator: TutorialNavGraphNavigator) =
    ReadLaterNavGraphNavigatorImpl(navigator)

interface TutorialNavGraphNavigator {
    fun onComplete()
}

class ReadLaterNavGraphNavigatorImpl internal constructor(
    navigator: TutorialNavGraphNavigator,
) : TutorialScreenNavigator,
    TutorialNavGraphNavigator by navigator
