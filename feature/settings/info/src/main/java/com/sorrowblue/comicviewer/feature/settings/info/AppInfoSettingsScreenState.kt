package com.sorrowblue.comicviewer.feature.settings.info

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.review.testing.FakeReviewManager
import com.mikepenz.aboutlibraries.LibsBuilder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import logcat.asLog
import logcat.logcat

internal interface AppInfoSettingsScreenState {
    fun launchReview()
    fun launchLicence()

    var uiState: SettingsAppInfoScreenUiState
}

@Composable
internal fun rememberAppInfoSettingsScreenState(context: Context = LocalContext.current): AppInfoSettingsScreenState =
    remember {
        AppInfoSettingsScreenStateImpl(context)
    }

private class AppInfoSettingsScreenStateImpl(private val context: Context) :
    AppInfoSettingsScreenState {
    override var uiState: SettingsAppInfoScreenUiState by mutableStateOf(
        SettingsAppInfoScreenUiState(
            versionName = context.packageInfo.versionName,
            buildAt = Instant.ofEpochMilli(BuildConfig.TIMESTAMP)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    )

    override fun launchReview() {
        val manager = FakeReviewManager(context)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(context as Activity, task.result)
                    .addOnCompleteListener { a ->
                        if (a.isSuccessful) {
                            logcat { "成功" }
                        } else {
                            logcat { a.exception?.asLog().toString() }
                            // TODO(ストアページにフォールバックする) "http://play.google.com/store/apps/details?id=${context.packageName}"
                        }
                    }
            } else {
                logcat { task.exception?.asLog().toString() }
                // TODO(ストアページにフォールバックする) "http://play.google.com/store/apps/details?id=${context.packageName}"
            }
        }
    }

    override fun launchLicence() {
        LibsBuilder().withActivityTitle(context.getString(R.string.settings_info_title_licence))
            .withSearchEnabled(true)
            .withEdgeToEdge(true).start(context)
    }
}

private val Context.packageInfo: PackageInfo
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        packageManager.getPackageInfo(packageName, 0)
    }
