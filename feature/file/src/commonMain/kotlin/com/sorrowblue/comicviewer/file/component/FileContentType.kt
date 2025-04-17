package com.sorrowblue.comicviewer.file.component

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuScope
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_action_change_grid_size
import comicviewer.feature.file.generated.resources.file_list_label_switch_grid_view
import comicviewer.feature.file.generated.resources.file_list_label_switch_list_view
import org.jetbrains.compose.resources.stringResource

@Composable
fun OverflowMenuScope.FileListDisplayItem(fileListDisplay: FileListDisplay, onClick: () -> Unit) {
    if (fileListDisplay == FileListDisplay.Grid) {
        OverflowMenuItem(
            text = stringResource(Res.string.file_list_label_switch_list_view),
            icon = ComicIcons.ViewList,
            onClick = onClick
        )
    } else {
        OverflowMenuItem(
            text = stringResource(Res.string.file_list_label_switch_grid_view),
            icon = ComicIcons.GridView,
            onClick = onClick
        )
    }
}

@Composable
fun OverflowMenuScope.GridSizeItem(fileListDisplay: FileListDisplay, onClick: () -> Unit) {
    if (fileListDisplay == FileListDisplay.Grid) {
        OverflowMenuItem(
            text = stringResource(Res.string.file_action_change_grid_size),
            icon = ComicIcons.Grid4x4,
            onClick = onClick
        )
    }
}
