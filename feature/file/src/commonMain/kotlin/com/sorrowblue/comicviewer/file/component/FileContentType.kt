package com.sorrowblue.comicviewer.file.component

import androidx.compose.material3.AppBarRowScope2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_action_change_grid_size
import comicviewer.feature.file.generated.resources.file_list_label_switch_grid_view
import comicviewer.feature.file.generated.resources.file_list_label_switch_list_view
import org.jetbrains.compose.resources.stringResource

fun AppBarRowScope2.fileListDisplayItem(fileListDisplay: FileListDisplay, onClick: () -> Unit) {
    clickableItem(
        autoDismiss = false,
        label = {
            Text(
                if (fileListDisplay == FileListDisplay.Grid) stringResource(Res.string.file_list_label_switch_list_view) else stringResource(
                    Res.string.file_list_label_switch_grid_view
                )
            )
        },
        icon = {
            Icon(
                if (fileListDisplay == FileListDisplay.Grid) ComicIcons.ViewList else ComicIcons.GridView,
                null
            )
        },
        onClick = onClick
    )
}

fun AppBarRowScope2.gridSizeItem(fileListDisplay: FileListDisplay, onClick: () -> Unit) {
    clickableItem(
        visible = fileListDisplay == FileListDisplay.Grid,
        autoDismiss = false,
        label = { Text(stringResource(Res.string.file_action_change_grid_size)) },
        icon = { Icon(ComicIcons.Grid4x4, null) },
        onClick = onClick
    )
}
