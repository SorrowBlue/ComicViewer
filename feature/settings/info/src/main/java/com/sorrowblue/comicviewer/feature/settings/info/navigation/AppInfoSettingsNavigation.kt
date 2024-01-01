package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreen
import com.sorrowblue.comicviewer.feature.settings.info.destinations.AppInfoSettingsScreenDestination

fun NavGraphBuilder.appInfoSettingsScreen(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    composable(AppInfoSettingsScreenDestination) {
        AppInfoSettingsScreen(
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}
