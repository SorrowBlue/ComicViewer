package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox

sealed interface FileContentType {

    val columns: GridCells

    data object List : FileContentType {
        override val columns get() = GridCells.Fixed(1)
    }

    data class Grid(val size: GridSize = GridSize.Medium) : FileContentType {

        override val columns: GridCells
            get() = GridCells.Adaptive(
                when (size) {
                    GridSize.Small -> 88.dp
                    GridSize.Medium -> 120.dp
                    GridSize.Large -> 180.dp
                }
            )
    }

    enum class GridSize {
        Small, Medium, Large
    }
}

fun FolderDisplaySettings.toFileContentLayout(): FileContentType {
    return when (display) {
        FolderDisplaySettings.Display.GRID -> FileContentType.Grid(
            when (columnSize) {
                FolderDisplaySettings.Size.SMALL -> FileContentType.GridSize.Small
                FolderDisplaySettings.Size.MEDIUM -> FileContentType.GridSize.Medium
                FolderDisplaySettings.Size.LARGE -> FileContentType.GridSize.Large
            }
        )

        FolderDisplaySettings.Display.LIST -> FileContentType.List
    }
}

@Composable
fun FileContentLayoutButton(fileContentType: FileContentType, onClick: () -> Unit) {
    when (fileContentType) {
        is FileContentType.Grid -> {
            PlainTooltipBox(
                tooltipContent = {
                    Text(stringResource(id = R.string.file_list_label_switch_list_view))
                }
            ) {
                IconButton(onClick) {
                    Icon(
                        ComicIcons.ViewList,
                        stringResource(id = R.string.file_list_label_switch_list_view)
                    )
                }
            }
        }

        FileContentType.List -> {
            PlainTooltipBox(
                tooltipContent = {
                    Text(stringResource(id = R.string.file_list_label_switch_grid_view))
                }
            ) {
                IconButton(onClick) {
                    Icon(
                        ComicIcons.GridView,
                        stringResource(id = R.string.file_list_label_switch_grid_view)
                    )
                }
            }
        }
    }
}
