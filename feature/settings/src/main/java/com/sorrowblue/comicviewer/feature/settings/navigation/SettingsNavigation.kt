package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.SettingsRoute
import com.sorrowblue.comicviewer.feature.settings.language.InAppLanguagePickerScreen

private const val SettingsRoute = "settings"
internal const val InAppLanguagePickerRoute = "settings/inapplanguagepicker"

fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onStartTutorialClick: () -> Unit,
    contentPadding: PaddingValues,
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

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(SettingsRoute, navOptions)
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
