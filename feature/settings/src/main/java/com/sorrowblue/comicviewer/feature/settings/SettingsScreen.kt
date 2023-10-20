package com.sorrowblue.comicviewer.feature.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.feature.settings.common.SettingsItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import kotlinx.collections.immutable.toPersistentList

enum class Settings2(
    override val title: Int,
    override val icon: ImageVector,
) : SettingsItem {
    Display(R.string.settings_label_display, ComicIcons.DisplaySettings),
    Folder(R.string.settings_label_folder, ComicIcons.FolderOpen),
    Viewer(R.string.settings_label_viewer, ComicIcons.Image),
    Security(R.string.settings_label_security, ComicIcons.Lock),
    App(R.string.settings_label_app, ComicIcons.Info),
    Tutorial(R.string.settings_label_tutorial, ComicIcons.Start),
    Language(R.string.settings_label_language, ComicIcons.Language),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onDisplayClick: () -> Unit = {},
    onFolderClick: () -> Unit = {},
    onViewerClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {},
    onAppInfoClick: () -> Unit = {},
    onAppLanguageClick: () -> Unit = {},
    onStartTutorialClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val list = remember { Settings2.entries.toPersistentList() }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            com.sorrowblue.comicviewer.feature.settings.common.SettingsTopAppBar(
                title = R.string.settings_title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsColumn(
            contentPadding = contentPadding
        ) {
            Setting(
                title = stringResource(id = R.string.settings_label_display),
                icon = ComicIcons.DisplaySettings,
                onClick = onDisplayClick
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
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APP_LOCALE_SETTINGS,
                                Uri.parse("package:${context.applicationInfo.packageName}")
                            )
                        )
                    } else {
                        onAppLanguageClick()
                    }
                }
            )
        }
    }
}
