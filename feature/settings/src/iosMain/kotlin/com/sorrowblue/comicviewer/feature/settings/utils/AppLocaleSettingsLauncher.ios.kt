package com.sorrowblue.comicviewer.feature.settings.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
internal actual fun rememberAppLocaleSettingsLauncher(): AppLocaleSettingsLauncher = remember {
    AppLocaleSettingsLauncher()
}

internal actual class AppLocaleSettingsLauncher {
    actual fun launch(fallback: () -> Unit) {
        val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url, emptyMap<Any?, Any>()) {}
        } else {
            fallback()
        }
    }
}
