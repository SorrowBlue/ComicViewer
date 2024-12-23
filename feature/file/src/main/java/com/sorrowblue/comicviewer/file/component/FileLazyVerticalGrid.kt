package com.sorrowblue.comicviewer.file.component

import android.os.Parcelable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.LazyPagingColumn
import com.sorrowblue.comicviewer.framework.ui.adaptive.LazyPagingColumnType
import com.sorrowblue.comicviewer.framework.ui.layout.blink
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.flowData
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileLazyVerticalGridUiState(
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val columnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
    val showThumbnails: Boolean = FolderDisplaySettingsDefaults.isSavedThumbnail,
    val fontSize: Int = FolderDisplaySettingsDefaults.fontSize,
    val imageScale: ImageScale = FolderDisplaySettingsDefaults.imageScale,
    val imageFilterQuality: ImageFilterQuality = FolderDisplaySettingsDefaults.imageFilterQuality,
) : Parcelable

@Composable
fun <T : File> FileLazyVerticalGrid(
    uiState: FileLazyVerticalGridUiState,
    lazyPagingItems: LazyPagingItems<T>,
    onItemClick: (T) -> Unit,
    onItemInfoClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    emphasisPath: String = "",
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    val contentScale by remember(uiState.imageScale) {
        mutableStateOf(
            when (uiState.imageScale) {
                ImageScale.Crop -> ContentScale.Crop
                ImageScale.Fit -> ContentScale.Fit
            }
        )
    }
    val filterQuality by remember(uiState.imageFilterQuality) {
        mutableStateOf(
            when (uiState.imageFilterQuality) {
                ImageFilterQuality.None -> FilterQuality.None
                ImageFilterQuality.Low -> FilterQuality.Low
                ImageFilterQuality.Medium -> FilterQuality.Medium
                ImageFilterQuality.High -> FilterQuality.High
            }
        )
    }
    val type by rememberLazyPagingColumnType(
        fileListDisplay = uiState.fileListDisplay,
        gridColumnSize = uiState.columnSize
    )
    LazyPagingColumn(
        state = state,
        lazyPagingItems = lazyPagingItems,
        contentPadding = contentPadding,
        type = type,
        modifier = modifier
    ) { _, item ->
        when (type) {
            LazyPagingColumnType.List -> {
                ListFile(
                    file = item,
                    onClick = { onItemClick(item) },
                    onLongClick = { onItemInfoClick(item) },
                    showThumbnail = uiState.showThumbnails,
                    fontSize = uiState.fontSize,
                    contentScale = contentScale,
                    filterQuality = filterQuality,
                    modifier = if (item.path == emphasisPath) {
                        Modifier.blink(ComicTheme.colorScheme.secondary, 0.0f..0.3f)
                    } else {
                        Modifier
                    }.animateItem()
                )
            }

            LazyPagingColumnType.ListMedium -> {
                ListFileCard(
                    file = item,
                    onClick = { onItemClick(item) },
                    onLongClick = { onItemInfoClick(item) },
                    showThumbnail = uiState.showThumbnails,
                    fontSize = uiState.fontSize,
                    contentScale = contentScale,
                    filterQuality = filterQuality,
                    modifier = if (item.path == emphasisPath) {
                        Modifier.blink(ComicTheme.colorScheme.secondary, 0.0f..0.3f)
                    } else {
                        Modifier
                    }.animateItem()
                )
            }

            is LazyPagingColumnType.Grid -> GridFile(
                file = item,
                onClick = { onItemClick(item) },
                onInfoClick = { onItemInfoClick(item) },
                showThumbnail = uiState.showThumbnails,
                fontSize = uiState.fontSize,
                contentScale = contentScale,
                filterQuality = filterQuality,
                modifier = if (item.path == emphasisPath) {
                    Modifier.blink(ComicTheme.colorScheme.secondary, 0.0f..0.3f)
                } else {
                    Modifier
                }.animateItem()
            )
        }
    }
}

@Composable
fun rememberLazyPagingColumnType(
    fileListDisplay: FileListDisplay,
    gridColumnSize: GridColumnSize,
): State<LazyPagingColumnType> {
    val scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo())
    val widthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    return remember(fileListDisplay, gridColumnSize) {
        mutableStateOf(
            when (fileListDisplay) {
                FileListDisplay.List -> if (scaffoldDirective.maxHorizontalPartitions == 1) LazyPagingColumnType.List else LazyPagingColumnType.ListMedium
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
                }.let(LazyPagingColumnType::Grid)
            }
        )
    }
}

@PreviewMultiScreen
@Composable
private fun PreviewGridFileLazyGrid() {
    val lazyPagingItems = PagingData.flowData<File> { fakeBookFile(it) }.collectAsLazyPagingItems()
    PreviewTheme {
        Scaffold {
            FileLazyVerticalGrid(
                uiState = FileLazyVerticalGridUiState(),
                lazyPagingItems = lazyPagingItems,
                onItemClick = {},
                onItemInfoClick = {},
                contentPadding = it
            )
        }
    }
}
