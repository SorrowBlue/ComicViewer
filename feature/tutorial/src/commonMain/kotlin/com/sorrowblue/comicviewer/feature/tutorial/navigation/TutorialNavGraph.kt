package com.sorrowblue.comicviewer.feature.tutorial.navigation

import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenNavigator
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory


interface TutorialNavGraphNavigator {
    fun onCompleteTutorial()
}

@Factory
internal class TutorialNavGraphNavigatorImpl(
    private val navigator: TutorialNavGraphNavigator,
) : TutorialScreenNavigator {

    override fun onCompleteTutorial() = navigator.onCompleteTutorial()
}

@Serializable
@NavGraph(startDestination = Tutorial::class)
data object TutorialNavGraph {

    @DestinationInGraph<Tutorial>
    object Include
}
