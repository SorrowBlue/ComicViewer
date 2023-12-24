package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.display.SettingsDisplayRoute

const val SettingsDisplayRoute = "settings/display"

fun NavGraphBuilder.settingsDisplayScreen(onBackClick: () -> Unit, contentPadding: PaddingValues) {
    composable(SettingsDisplayRoute) {
        SettingsDisplayRoute(
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}
