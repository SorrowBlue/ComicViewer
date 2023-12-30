package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.info.SettingsAppInfoRoute

const val SettingsAppInfoRoute = "settings/app_info"

fun NavGraphBuilder.settingsAppInfoScreen(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    composable(SettingsAppInfoRoute) {
        SettingsAppInfoRoute(
            onBackClick = onBackClick,
            contentPadding = contentPadding
        )
    }
}
