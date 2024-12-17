package com.sorrowblue.comicviewer.file.component

import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuScope
import kotlinx.parcelize.Parcelize

sealed class FileContentType : Parcelable {

    abstract val columns: GridCells

    @Parcelize
    data object List : FileContentType() {
        override val columns get() = GridCells.Fixed(1)
    }

    @Parcelize
    data object ListMedium : FileContentType() {
        override val columns get() = GridCells.Adaptive(300.dp)
    }

    @Parcelize
    data class Grid(val minSize: Int) : FileContentType() {
        override val columns get() = GridCells.Adaptive(minSize.dp)
    }
}

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

@Composable
fun rememberFileContentType(
    fileListDisplay: FileListDisplay,
    gridColumnSize: GridColumnSize,
): State<FileContentType> {
    val scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo())
    val widthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    return remember(fileListDisplay, gridColumnSize) {
        mutableStateOf(
            when (fileListDisplay) {
                FileListDisplay.List -> if (scaffoldDirective.maxHorizontalPartitions == 1) FileContentType.List else FileContentType.ListMedium
                FileListDisplay.Grid -> when (widthSizeClass) {
                    WindowWidthSizeClass.COMPACT -> when (gridColumnSize) {
                        GridColumnSize.Medium -> 120
                        GridColumnSize.Large -> 180
                    }

                    WindowWidthSizeClass.MEDIUM -> when (gridColumnSize) {
                        GridColumnSize.Medium -> 160
                        GridColumnSize.Large -> 200
                    }

                    WindowWidthSizeClass.EXPANDED -> when (gridColumnSize) {
                        GridColumnSize.Medium -> 160
                        GridColumnSize.Large -> 200
                    }

                    else -> when (gridColumnSize) {
                        GridColumnSize.Medium -> 120
                        GridColumnSize.Large -> 180
                    }
                }.let(FileContentType::Grid)
            }
        )
    }
}
