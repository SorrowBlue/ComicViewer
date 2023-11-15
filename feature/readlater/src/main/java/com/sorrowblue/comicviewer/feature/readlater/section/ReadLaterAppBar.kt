package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.runtime.Composable
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
                        text = "Change Grid size",
                        icon = ComicIcons.Grid4x4,
                        onClick = onGridSizeClick
                    )
                }
                OverflowMenuItem(
                    text = "Clear Read Later",
                    icon = ComicIcons.ClearAll,
                    onClick = onClearAllClick
                )
                OverflowMenuItem(
                    text = "Settings",
                    icon = ComicIcons.Settings,
                    onClick = onSettingsClick
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    )
}
