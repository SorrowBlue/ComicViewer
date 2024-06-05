package com.sorrowblue.comicviewer.feature.settings.info

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Stable
internal interface AppInfoSettingsScreenState {
    fun launchReview()
    var uiState: SettingsAppInfoScreenUiState
}

@Composable
internal fun rememberAppInfoSettingsScreenState(
    context: Context = LocalContext.current,
): AppInfoSettingsScreenState = remember {
    AppInfoSettingsScreenStateImpl(context = context)
}

private class AppInfoSettingsScreenStateImpl(
    private val context: Context,
) : AppInfoSettingsScreenState {

    override var uiState: SettingsAppInfoScreenUiState by mutableStateOf(
        SettingsAppInfoScreenUiState(
            versionName = context.packageInfo.versionName,
            buildAt = Instant.ofEpochMilli(BuildConfig.TIMESTAMP)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    )

    override fun launchReview() {
        val applicationId = context.packageName.replace(".${BuildConfig.BUILD_TYPE}", "")
        CustomTabsIntent.Builder().build().launchUrl(
            context as Activity,
            "http://play.google.com/store/apps/details?id=$applicationId".toUri()
        )
    }
}

private val Context.packageInfo: PackageInfo
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        packageManager.getPackageInfo(packageName, 0)
    }
