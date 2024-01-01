package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreen
import com.sorrowblue.comicviewer.feature.settings.display.destinations.DisplaySettingsScreenDestination

fun NavGraphBuilder.settingsDisplayScreen(onBackClick: () -> Unit, contentPadding: PaddingValues) {
    composable(DisplaySettingsScreenDestination) {
        DisplaySettingsScreen(
            savedStateHandle = navBackStackEntry.savedStateHandle,
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}
