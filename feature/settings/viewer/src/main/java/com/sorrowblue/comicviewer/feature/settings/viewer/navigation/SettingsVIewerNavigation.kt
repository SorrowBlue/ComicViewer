package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.viewer.SettingsViewerRoute

const val SettingsViewerRoute = "settings/viewer"

fun NavGraphBuilder.settingsViewerScreen(onBackClick: () -> Unit, contentPadding: PaddingValues) {
    composable(SettingsViewerRoute) {
        SettingsViewerRoute(
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}
