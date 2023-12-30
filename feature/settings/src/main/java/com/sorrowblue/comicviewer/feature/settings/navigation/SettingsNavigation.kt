package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.SettingsRoute
import com.sorrowblue.comicviewer.feature.settings.language.InAppLanguagePickerScreen
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

const val SettingsRoute = "settings"
const val SettingsNavigationRoute = "settings_graph"
internal const val InAppLanguagePickerRoute = "settings/inapplanguagepicker"

context(ComposeValue)
fun NavGraphBuilder.settingsNavigation(
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onStartTutorialClick: () -> Unit,
) {
    animatedNavigation(
        startDestination = SettingsRoute,
        route = SettingsNavigationRoute,
        transitions = listOf(
            ComposeTransition(
                SettingsNavigationRoute,
                null,
                ComposeTransition.Type.ContainerTransform
            )
        )
    ) {
        composable(SettingsRoute) { navBackStackEntry ->
            with(navBackStackEntry) {
                SettingsRoute(
                    onBackClick = onBackClick,
                    onChangeAuthEnabled = onChangeAuthEnabled,
                    onPasswordChangeClick = onPasswordChangeClick,
                    onStartTutorialClick = onStartTutorialClick,
                    contentPadding = contentPadding
                )
            }
        }
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(SettingsNavigationRoute, navOptions)
}

internal fun NavGraphBuilder.inAppLanguagePickerScreen(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    composable(InAppLanguagePickerRoute) {
        InAppLanguagePickerScreen(
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}
