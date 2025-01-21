package com.sorrowblue.comicviewer.feature.tutorial.navigation

import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenNavigator
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

@Serializable
data object TutorialNavGraph

interface TutorialNavGraphNavigator {
    fun onCompleteTutorial()
}

@Factory
internal class TutorilaNavGraphNavigatorImpl(
    private val navigator: TutorialNavGraphNavigator,
) : TutorialScreenNavigator {

    override fun onCompleteTutorial() = navigator.onCompleteTutorial()
}

@NavGraph<TutorialNavGraph>(startDestination = Tutorial::class)
internal class TutorialNavigation {

    @DestinationInGraph<Tutorial>
    companion object
}
