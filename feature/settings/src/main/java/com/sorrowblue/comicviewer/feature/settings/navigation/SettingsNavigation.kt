package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.feature.settings.SettingsScreen
import com.sorrowblue.comicviewer.feature.settings.destinations.InAppLanguagePickerScreenDestination
import com.sorrowblue.comicviewer.feature.settings.destinations.SettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.language.InAppLanguagePickerScreen
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

const val SettingsNavigationRoute = "settings_graph"

context(ComposeValue)
fun NavGraphBuilder.settingsNavigation(
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onStartTutorialClick: () -> Unit,
) {
    animatedNavigation(
        startDestination = SettingsScreenDestination.route,
        route = SettingsNavigationRoute,
        transitions = listOf(
            ComposeTransition(
                SettingsNavigationRoute,
                null,
                ComposeTransition.Type.ContainerTransform
            )
        )
    ) {
        composable(SettingsScreenDestination) {
            SettingsScreen(
                savedStateHandle = navBackStackEntry.savedStateHandle,
                onBackClick = onBackClick,
                onChangeAuthEnabled = onChangeAuthEnabled,
                onPasswordChangeClick = onPasswordChangeClick,
                onStartTutorialClick = onStartTutorialClick,
                contentPadding = contentPadding,
            )
        }
    }
}

fun NavController.navigateToSettings() {
    navigate(InAppLanguagePickerScreenDestination)
}

internal fun NavGraphBuilder.inAppLanguagePickerScreen(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    composable(InAppLanguagePickerScreenDestination) {
        InAppLanguagePickerScreen(
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}
