package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

sealed interface FileContentLayout {

    val columns: StaggeredGridCells

    data object List : FileContentLayout {
        override val columns get() = StaggeredGridCells.Fixed(1)
    }

    data class Grid(val size: GridSize = GridSize.Medium) : FileContentLayout {

        override val columns: StaggeredGridCells
            get() = StaggeredGridCells.Adaptive(
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

fun FolderDisplaySettings.toFileContentLayout(): FileContentLayout {
    return when (display) {
        FolderDisplaySettings.Display.GRID -> FileContentLayout.Grid(
            when (columnSize) {
                FolderDisplaySettings.Size.SMALL -> FileContentLayout.GridSize.Small
                FolderDisplaySettings.Size.MEDIUM -> FileContentLayout.GridSize.Medium
                FolderDisplaySettings.Size.LARGE -> FileContentLayout.GridSize.Large
            }
        )
        FolderDisplaySettings.Display.LIST -> FileContentLayout.List
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileContentLayoutButton(fileContentLayout: FileContentLayout, onClick: () -> Unit) {
    when (fileContentLayout) {
        is FileContentLayout.Grid -> {
            PlainTooltipBox(tooltip = { Text(stringResource(id = R.string.file_list_label_switch_list_view)) }) {
                IconButton(onClick, Modifier.tooltipAnchor()) {
                    Icon(
                        ComicIcons.ViewList,
                        stringResource(id = R.string.file_list_label_switch_list_view)
                    )
                }
            }
        }

        FileContentLayout.List -> {
            PlainTooltipBox(tooltip = { Text(stringResource(id = R.string.file_list_label_switch_grid_view)) }) {
                IconButton(onClick, Modifier.tooltipAnchor()) {
                    Icon(
                        ComicIcons.GridView,
                        stringResource(id = R.string.file_list_label_switch_grid_view)
                    )
                }
            }
        }
    }
}
