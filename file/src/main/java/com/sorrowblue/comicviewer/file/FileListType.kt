package com.sorrowblue.comicviewer.file

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.GridView
import androidx.compose.material.icons.twotone.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings

sealed interface FileListType {

    val spanCount: Int

    data class Grid(override val spanCount: Int) : FileListType
    data object List : FileListType {
        override val spanCount = 1
    }

    companion object {
        fun from(display: FolderDisplaySettings.Display, span: Int) = when (display) {
            FolderDisplaySettings.Display.GRID -> Grid(span)
            FolderDisplaySettings.Display.LIST -> List
        }
    }
}

sealed interface FileListType2 {
    data class Grid(val size: FolderDisplaySettings.Size) : FileListType2
    data object List : FileListType2
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListTypeIconButton(fileListType: FileListType, onClick: () -> Unit) {
    when (fileListType) {
        is FileListType.Grid -> {
            PlainTooltipBox(tooltip = { Text(stringResource(id = R.string.file_list_label_switch_list_view)) }) {
                IconButton(onClick, Modifier.tooltipAnchor()) {
                    Icon(
                        Icons.TwoTone.ViewList,
                        stringResource(id = R.string.file_list_label_switch_list_view)
                    )
                }
            }
        }

        FileListType.List -> {
            PlainTooltipBox(tooltip = { Text(stringResource(id = R.string.file_list_label_switch_grid_view)) }) {
                IconButton(onClick, Modifier.tooltipAnchor()) {
                    Icon(
                        Icons.TwoTone.GridView,
                        stringResource(id = R.string.file_list_label_switch_grid_view)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListTypeIconButton2(fileListType: FileListType2, onClick: () -> Unit) {
    when (fileListType) {
        is FileListType2.Grid -> {
            PlainTooltipBox(tooltip = { Text(stringResource(id = R.string.file_list_label_switch_list_view)) }) {
                IconButton(onClick, Modifier.tooltipAnchor()) {
                    Icon(
                        Icons.TwoTone.ViewList,
                        stringResource(id = R.string.file_list_label_switch_list_view)
                    )
                }
            }
        }

        FileListType2.List -> {
            PlainTooltipBox(tooltip = { Text(stringResource(id = R.string.file_list_label_switch_grid_view)) }) {
                IconButton(onClick, Modifier.tooltipAnchor()) {
                    Icon(
                        Icons.TwoTone.GridView,
                        stringResource(id = R.string.file_list_label_switch_grid_view)
                    )
                }
            }
        }
    }
}
