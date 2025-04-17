package com.sorrowblue.comicviewer.feature.settings.utils

import androidx.compose.runtime.Composable

@Composable
internal expect fun rememberAppLocaleSettingsLauncher(): AppLocaleSettingsLauncher

internal expect class AppLocaleSettingsLauncher {

    fun launch(fallback: () -> Unit)
}
