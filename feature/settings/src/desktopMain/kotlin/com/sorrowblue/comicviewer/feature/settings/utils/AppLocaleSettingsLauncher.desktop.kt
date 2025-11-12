package com.sorrowblue.comicviewer.feature.settings.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberAppLocaleSettingsLauncher(): AppLocaleSettingsLauncher = remember {
    AppLocaleSettingsLauncher()
}

internal actual class AppLocaleSettingsLauncher {
    actual fun launch(fallback: () -> Unit) {
        fallback()
    }
}
