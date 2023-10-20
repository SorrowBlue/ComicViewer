package com.sorrowblue.comicviewer.feature.settings.info

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.review.testing.FakeReviewManager
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sorrowblue.comicviewer.feature.settings.common.SettingsItem
import com.sorrowblue.comicviewer.feature.settings.common.SettingsListContents
import com.sorrowblue.comicviewer.feature.settings.common.SettingsTopAppBar
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
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
            listOf(
                SettingsAppInfo.Version(context.packageInfo.versionName),
                SettingsAppInfo.Build(
                    Instant.ofEpochMilli(BuildConfig.TIMESTAMP)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                ),
                SettingsAppInfo.License,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
                SettingsAppInfo.RateApp,
            ).toPersistentList()
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
//                            extraNavController.launchUrl("http://play.google.com/store/apps/details?id=${context.packageName}")
                        }
                    }
            } else {
                logcat { task.exception?.asLog().toString() }
//                extraNavController.launchUrl("http://play.google.com/store/apps/details?id=${context.packageName}")
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

sealed interface SettingsAppInfo : SettingsItem {
    data class Version(
        override val text: String,
        override val title: Int = R.string.settings_info_label_version,
    ) : SettingsAppInfo

    data class Build(
        override val text: String,
        override val title: Int = R.string.settings_info_label_build,
    ) : SettingsAppInfo

    data object License : SettingsAppInfo {
        override val title = R.string.settings_info_label_license
    }

    data object RateApp : SettingsAppInfo {
        override val title = R.string.settings_info_label_rate
        override val icon = ComicIcons.Star
    }
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
    val list: PersistentList<SettingsAppInfo>,
)

@OptIn(ExperimentalMaterial3Api::class)
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
            SettingsTopAppBar(
                title = R.string.settings_info_title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsListContents(
            contentPadding = contentPadding,
            list = uiState.list,
            onClick = {
                when (it) {
                    is SettingsAppInfo.Build -> Unit
                    is SettingsAppInfo.Version -> Unit
                    SettingsAppInfo.License -> onLicenceClick()
                    SettingsAppInfo.RateApp -> onRateAppClick()
                }
            }
        )
    }
}
