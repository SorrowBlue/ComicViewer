package com.sorrowblue.comicviewer.feature.settings.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.uri.Uri
import androidx.core.net.toUri

@Composable
internal actual fun rememberAppLocaleSettingsLauncher(): AppLocaleSettingsLauncher {
    return AppLocaleSettingsLauncher(LocalContext.current)
}

internal actual class AppLocaleSettingsLauncher(private val context: Context) {

    actual fun launch(fallback: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runCatching {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APP_LOCALE_SETTINGS,
                        "package:${context.applicationInfo.packageName}".toUri()
                    )
                )
            }.onFailure {
                fallback()
            }
        } else {
            fallback()
        }
    }
}
