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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.play.core.review.testing.FakeReviewManager
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import logcat.asLog
import logcat.logcat

interface AppInfoSettingsScreenState {
    fun launchReview()
    fun launchLicence()

    var uiState: SettingsAppInfoScreenUiState
}

class AppInfoSettingsScreenStateImpl(private val context: Context) : AppInfoSettingsScreenState {
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
        LibsBuilder().withActivityTitle("Licence").withSearchEnabled(true)
            .withEdgeToEdge(true).start(context)
    }
}

@Composable
fun rememberAppInfoSettingsScreenState(context: Context = LocalContext.current): AppInfoSettingsScreenState =
    remember {
        AppInfoSettingsScreenStateImpl(context)
    }

val Context.packageInfo: PackageInfo
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        packageManager.getPackageInfo(packageName, 0)
    }

@Composable
internal fun SettingsAppInfoRoute(
    onBackClick: () -> Unit,
    state: AppInfoSettingsScreenState = rememberAppInfoSettingsScreenState(),
) {
    SettingsAppInfoScreen(
        state.uiState,
        onBackClick = onBackClick,
        onLicenceClick = state::launchLicence,
        onRateAppClick = {
            state.launchReview()
        }
    )
}

data class SettingsAppInfoScreenUiState(
    val versionName: String = "",
    val buildAt: String,
)

@Composable
private fun SettingsAppInfoScreen(
    uiState: SettingsAppInfoScreenUiState,
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
    onRateAppClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = R.string.settings_info_title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsColumn(contentPadding = contentPadding) {
            Setting(
                title = stringResource(id = R.string.settings_info_label_version),
                onClick = { },
                summary = uiState.versionName
            )
            Setting(
                title = stringResource(id = R.string.settings_info_label_build),
                onClick = { },
                summary = uiState.buildAt
            )
            Setting(
                title = R.string.settings_info_label_license,
                onClick = onLicenceClick,
            )
            Setting(
                title = R.string.settings_info_label_rate,
                onClick = onRateAppClick,
                icon = ComicIcons.Star
            )
        }
    }
}
