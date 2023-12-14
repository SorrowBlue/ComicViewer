package com.sorrowblue.comicviewer.file.component

import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import kotlinx.parcelize.Parcelize

sealed interface FileContentType : Parcelable {

    val columns: GridCells
    val columns2: StaggeredGridCells
    val columns3: GridCells
        @Composable get

    @Parcelize
    data object List : FileContentType {
        override val columns get() = GridCells.Fixed(1)
        override val columns2 get() = StaggeredGridCells.Fixed(1)
        override val columns3: GridCells
            @Composable get() = GridCells.Fixed(1)

    }

    @Parcelize
    data class Grid(val size: GridSize = GridSize.Medium) : FileContentType {

        override val columns: GridCells
            get() = GridCells.Adaptive(
                when (size) {
                    GridSize.Medium -> 120.dp
                    GridSize.Large -> 180.dp
                }
            )

        override val columns2
            get() = StaggeredGridCells.Adaptive(
                when (size) {
                    GridSize.Medium -> 120.dp
                    GridSize.Large -> 180.dp
                }
            )
        override val columns3: GridCells
            @Composable get() {
                val widthSizeClass = LocalWindowSize.current.widthSizeClass
                return when (widthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        when (size) {
                            GridSize.Medium -> 120.dp
                            GridSize.Large -> 180.dp
                        }
                    }

                    WindowWidthSizeClass.Medium -> {
                        when (size) {
                            GridSize.Medium -> 160.dp
                            GridSize.Large -> 200.dp
                        }
                    }

                    WindowWidthSizeClass.Expanded -> {
                        when (size) {
                            GridSize.Medium -> 160.dp
                            GridSize.Large -> 200.dp
                        }
                    }

                    else -> {
                        when (size) {
                            GridSize.Medium -> 120.dp
                            GridSize.Large -> 180.dp
                        }
                    }
                }.let {
                    GridCells.Adaptive(it)
                }
            }
    }

    enum class GridSize {
        Medium, Large
    }
}

fun FolderDisplaySettings.toFileContentLayout(): FileContentType {
    return when (display) {
        FolderDisplaySettings.Display.GRID -> FileContentType.Grid(
            when (columnSize) {
                FolderDisplaySettings.Size.MEDIUM -> FileContentType.GridSize.Medium
                FolderDisplaySettings.Size.LARGE -> FileContentType.GridSize.Large
            }
        )

        FolderDisplaySettings.Display.LIST -> FileContentType.List
    }
}

@Composable
fun FileContentLayoutButton(
    fileContentType: FileContentType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (fileContentType) {
        is FileContentType.Grid -> {
            PlainTooltipBox(
                tooltipContent = {
                    Text(stringResource(id = R.string.file_list_label_switch_list_view))
                },
                modifier = modifier
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
                },
                modifier = modifier
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
