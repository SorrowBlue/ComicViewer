package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.readlater.R
import com.sorrowblue.comicviewer.file.component.FileContentLayoutButton
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState

@Composable
internal fun ReadLaterAppBar(
    fileContentType: FileContentType,
    topAppBarScrollBehavior: TopAppBarScrollBehavior? = null,
    onFileContentLayoutClick: () -> Unit = {},
    onGridSizeClick: () -> Unit = {},
    onClearAllClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    TopAppBar(
        title = R.string.readlater_title,
        actions = {
            FileContentLayoutButton(
                fileContentType = fileContentType,
                onClick = onFileContentLayoutClick
            )
            OverflowMenu(state = rememberOverflowMenuState()) {
                if (fileContentType is FileContentType.Grid) {
                    OverflowMenuItem(
                        text = stringResource(id = com.sorrowblue.comicviewer.feature.folder.R.string.folder_action_change_grid_size),
                        icon = ComicIcons.Grid4x4,
                        onClick = onGridSizeClick
                    )
                }
                OverflowMenuItem(
                    text = stringResource(R.string.readlater_action_clear_read_later),
                    icon = ComicIcons.ClearAll,
                    onClick = onClearAllClick
                )
                OverflowMenuItem(
                    text = stringResource(id = com.sorrowblue.comicviewer.framework.ui.R.string.framework_ui_label_settings),
                    icon = ComicIcons.Settings,
                    onClick = onSettingsClick
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    )
}
