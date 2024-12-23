package com.sorrowblue.comicviewer.file.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuScope

@Composable
fun OverflowMenuScope.FileListDisplayItem(fileListDisplay: FileListDisplay, onClick: () -> Unit) {
    if (fileListDisplay == FileListDisplay.Grid) {
        OverflowMenuItem(
            text = stringResource(id = R.string.file_list_label_switch_list_view),
            icon = ComicIcons.ViewList,
            onClick = onClick
        )
    } else {
        OverflowMenuItem(
            text = stringResource(id = R.string.file_list_label_switch_grid_view),
            icon = ComicIcons.GridView,
            onClick = onClick
        )
    }
}

@Composable
fun OverflowMenuScope.GridSizeItem(fileListDisplay: FileListDisplay, onClick: () -> Unit) {
    if (fileListDisplay == FileListDisplay.Grid) {
        OverflowMenuItem(
            text = stringResource(R.string.file_action_change_grid_size),
            icon = ComicIcons.Grid4x4,
            onClick = onClick
        )
    }
}
