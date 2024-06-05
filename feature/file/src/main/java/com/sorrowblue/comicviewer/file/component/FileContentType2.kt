package com.sorrowblue.comicviewer.file.component

import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuScope
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import logcat.logcat

sealed class FileContentType2 : Parcelable {

    @IgnoredOnParcel
    abstract val columns: GridCells

    @Parcelize
    data object List : FileContentType2() {
        @IgnoredOnParcel
        override val columns = GridCells.Fixed(1)
    }

    @Parcelize
    data object ListMedium : FileContentType2() {
        @IgnoredOnParcel
        override val columns = GridCells.Fixed(1)
    }

    @Parcelize
    data class Grid(val minSize: Int) : FileContentType2() {

        override val columns: GridCells
            get() = GridCells.Adaptive(minSize.dp)
    }
}

@Composable
fun OverflowMenuScope.ChangeGridSize(fileContentType: FileContentType2, onClick: () -> Unit) {
    if (fileContentType is FileContentType2.Grid) {
        OverflowMenuItem(
            text = stringResource(R.string.file_action_change_grid_size),
            icon = ComicIcons.Grid4x4,
            onClick = onClick
        )
    }
}

@Composable
fun OverflowMenuScope.FileContentType(fileContentType: FileContentType2, onClick: () -> Unit) {
    if (fileContentType is FileContentType2.Grid) {
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
fun rememberFileContentType(
    display: FolderDisplaySettings.Display,
    columnSize: FolderDisplaySettings.ColumnSize,
): State<FileContentType2> {
    val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
    val widthSizeClass = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass
    val isCompact = widthSizeClass == WindowWidthSizeClass.COMPACT
    return remember(display, columnSize) {
        logcat("APPAPP") { "rememberFileContentType" }
        mutableStateOf(
            when (display) {
                FolderDisplaySettings.Display.List -> if (isCompact) FileContentType2.List else FileContentType2.ListMedium
                FolderDisplaySettings.Display.Grid -> when (widthSizeClass) {
                    WindowWidthSizeClass.COMPACT -> when (columnSize) {
                        FolderDisplaySettings.ColumnSize.Medium -> 120
                        FolderDisplaySettings.ColumnSize.Large -> 180
                    }

                    WindowWidthSizeClass.MEDIUM -> when (columnSize) {
                        FolderDisplaySettings.ColumnSize.Medium -> 160
                        FolderDisplaySettings.ColumnSize.Large -> 200
                    }

                    WindowWidthSizeClass.EXPANDED -> when (columnSize) {
                        FolderDisplaySettings.ColumnSize.Medium -> 160
                        FolderDisplaySettings.ColumnSize.Large -> 200
                    }

                    else -> when (columnSize) {
                        FolderDisplaySettings.ColumnSize.Medium -> 120
                        FolderDisplaySettings.ColumnSize.Large -> 180
                    }
                }.let(FileContentType2::Grid)
            }
        )
    }
}
