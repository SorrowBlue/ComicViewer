package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.settings.Settings
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenNavigator
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

@Serializable
@NavGraph(startDestination = Settings::class, transition = SettingsNavGraphTransition::class)
data object SettingsNavGraph {

    @DestinationInGraph<Authentication>
    @DestinationInGraph<Settings>
    @DestinationInGraph<Tutorial>
    object Include
}

object SettingsNavGraphTransition : DestinationTransitions() {
    override val transitions: List<TransitionsConfigure> = listOf(
        TransitionsConfigure(
            SettingsNavGraph::class,
            null,
            TransitionsConfigure.Type.ContainerTransform
        ),
        TransitionsConfigure(
            Settings::class,
            Authentication::class,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            Settings::class,
            Tutorial::class,
            TransitionsConfigure.Type.SharedAxisY
        )
    )
}

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
