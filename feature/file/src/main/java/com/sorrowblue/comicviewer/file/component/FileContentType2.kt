package com.sorrowblue.comicviewer.file.component

import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.framework.ui.LocalWindowSize
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed class FileContentType2 : Parcelable {

    @IgnoredOnParcel
    abstract val columns: GridCells

    @Parcelize
    data object List : FileContentType2() {
        override val columns = GridCells.Fixed(1)
    }

    @Parcelize
    data object ListMedium : FileContentType2() {
        override val columns = GridCells.Fixed(1)
    }

    @Parcelize
    data class Grid(val minSize: Int) : FileContentType2() {

        override val columns: GridCells
            get() = GridCells.Adaptive(minSize.dp)
    }
}

@Composable
fun rememberFileContentType(
    display: FolderDisplaySettings.Display,
    columnSize: FolderDisplaySettings.ColumnSize,
): State<FileContentType2> {
    val widthSizeClass = LocalWindowSize.current.windowWidthSizeClass
    val isCompact = widthSizeClass == WindowWidthSizeClass.COMPACT
    return remember(display, columnSize) {
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
