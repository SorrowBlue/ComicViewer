package com.sorrowblue.comicviewer.feature.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior

@Composable
internal fun SettingsRoute(
    onBackClick: () -> Unit,
    onDisplayClick: () -> Unit,
    onFolderClick: () -> Unit,
    onViewerClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onAppLanguageClickFallback: () -> Unit,
    onStartTutorialClick: () -> Unit,
    state: SettingsScreenState = rememberSettingsScreenState(),
) {
    SettingsScreen(
        onBackClick = onBackClick,
        onDisplayClick = onDisplayClick,
        onFolderClick = onFolderClick,
        onViewerClick = onViewerClick,
        onSecurityClick = onSecurityClick,
        onAppInfoClick = onAppInfoClick,
        onAppLanguageClick = {
            state.onAppLanguageClick(fallback = onAppLanguageClickFallback)
        },
        onStartTutorialClick = onStartTutorialClick
    )
}

@Stable
internal class SettingsScreenState(private val context: Context) {
    fun onAppLanguageClick(fallback: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.startActivity(
                Intent(
                    Settings.ACTION_APP_LOCALE_SETTINGS,
                    Uri.parse("package:${context.applicationInfo.packageName}")
                )
            )
        } else {
            fallback()
        }
    }
}

@Composable
private fun rememberSettingsScreenState(context: Context = LocalContext.current) = remember {
    SettingsScreenState(context = context)
}

@Composable
private fun SettingsScreen(
    onBackClick: () -> Unit,
    onDisplayClick: () -> Unit,
    onFolderClick: () -> Unit,
    onViewerClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onAppLanguageClick: () -> Unit,
    onStartTutorialClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = R.string.settings_title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsColumn(contentPadding = contentPadding) {
            Setting(
                title = stringResource(id = R.string.settings_label_display),
                icon = ComicIcons.DisplaySettings,
                onClick = onDisplayClick,
            )
            Setting(
                title = stringResource(id = R.string.settings_label_folder),
                icon = ComicIcons.FolderOpen,
                onClick = onFolderClick
            )
            Setting(
                title = stringResource(id = R.string.settings_label_viewer),
                icon = ComicIcons.Image,
                onClick = onViewerClick
            )
            Setting(
                title = stringResource(id = R.string.settings_label_security),
                icon = ComicIcons.Lock,
                onClick = onSecurityClick
            )
            Setting(
                title = stringResource(id = R.string.settings_label_app),
                icon = ComicIcons.Info,
                onClick = onAppInfoClick
            )
            Setting(
                title = stringResource(id = R.string.settings_label_tutorial),
                icon = ComicIcons.Start,
                onClick = onStartTutorialClick
            )
            Setting(
                title = stringResource(id = R.string.settings_label_language),
                icon = ComicIcons.Language,
                onClick = onAppLanguageClick
            )
        }
    }
}
