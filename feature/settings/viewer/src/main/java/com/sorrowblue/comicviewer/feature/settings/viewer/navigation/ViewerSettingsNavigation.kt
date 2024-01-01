package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreen
import com.sorrowblue.comicviewer.feature.settings.viewer.destinations.ViewerSettingsScreenDestination

fun NavGraphBuilder.viewerSettingsScreen(onBackClick: () -> Unit, contentPadding: PaddingValues) {
    composable(ViewerSettingsScreenDestination) {
        ViewerSettingsScreen(
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}
