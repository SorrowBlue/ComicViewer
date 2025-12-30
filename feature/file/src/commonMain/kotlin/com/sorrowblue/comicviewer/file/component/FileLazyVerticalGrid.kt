package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
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
import androidx.window.core.layout.WindowSizeClass
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.layout.blink
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingColumn
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiplatform
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import kotlinx.serialization.Serializable

@Serializable
data class FileLazyVerticalGridUiState(
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val columnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
    val showThumbnails: Boolean = FolderDisplaySettingsDefaults.SavedThumbnail,
    val fontSize: Int = FolderDisplaySettingsDefaults.FontSize,
    val imageScale: ImageScale = FolderDisplaySettingsDefaults.imageScale,
    val imageFilterQuality: ImageFilterQuality = FolderDisplaySettingsDefaults.imageFilterQuality,
)

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
            },
        )
    }
    val filterQuality by remember(uiState.imageFilterQuality) {
        mutableStateOf(
            when (uiState.imageFilterQuality) {
                ImageFilterQuality.None -> FilterQuality.None
                ImageFilterQuality.Low -> FilterQuality.Low
                ImageFilterQuality.Medium -> FilterQuality.Medium
                ImageFilterQuality.High -> FilterQuality.High
            },
        )
    }
    val type by rememberLazyPagingColumnType(
        fileListDisplay = uiState.fileListDisplay,
        gridColumnSize = uiState.columnSize,
    )
    LazyPagingColumn(
        state = state,
        lazyPagingItems = lazyPagingItems,
        contentPadding = contentPadding,
        type = type,
        modifier = modifier,
    ) { _, item ->
        when (type) {
            LazyPagingColumn.List -> {
                ListFile(
                    file = item,
                    onLongClick = { onItemInfoClick(item) },
                    showThumbnail = uiState.showThumbnails,
                    fontSize = uiState.fontSize,
                    contentScale = contentScale,
                    filterQuality = filterQuality,
                    modifier = if (item.path == emphasisPath) {
                        Modifier.blink(ComicTheme.colorScheme.secondary, 0.0f..0.3f)
                    } else {
                        Modifier
                    }.animateItem().clickable { onItemClick(item) },
                )
            }

            LazyPagingColumn.ListMedium -> {
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
                    }.animateItem(),
                )
            }

            is LazyPagingColumn.Grid -> GridFile(
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
                }.animateItem(),
            )
        }
    }
}

@Composable
fun rememberLazyPagingColumnType(
    fileListDisplay: FileListDisplay,
    gridColumnSize: GridColumnSize,
): State<LazyPagingColumn> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo(),
    )
    return remember(fileListDisplay, gridColumnSize) {
        mutableStateOf(
            when (fileListDisplay) {
                FileListDisplay.List -> if (navigationSuiteType.isNavigationBar) {
                    LazyPagingColumn.List
                } else {
                    LazyPagingColumn.ListMedium
                }

                FileListDisplay.Grid -> when {
                    windowSizeClass.isWidthAtLeastBreakpoint(
                        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
                    ) -> when (gridColumnSize) {
                        GridColumnSize.Medium -> 160
                        GridColumnSize.Large -> 200
                    }

                    windowSizeClass.isWidthAtLeastBreakpoint(
                        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                    ) -> when (gridColumnSize) {
                        GridColumnSize.Medium -> 160
                        GridColumnSize.Large -> 200
                    }

                    else -> when (gridColumnSize) {
                        GridColumnSize.Medium -> 120
                        GridColumnSize.Large -> 180
                    }
                }.let(LazyPagingColumn::Grid)
            },
        )
    }
}

@PreviewMultiplatform
@Composable
private fun GridFileLazyGridPreview() {
    val lazyPagingItems = PagingData.flowData<File> { fakeBookFile(it) }.collectAsLazyPagingItems()
    PreviewTheme {
        Scaffold {
            FileLazyVerticalGrid(
                uiState = FileLazyVerticalGridUiState(),
                lazyPagingItems = lazyPagingItems,
                onItemClick = {},
                onItemInfoClick = {},
                contentPadding = it,
            )
        }
    }
}

@PreviewMultiplatform
@Composable
private fun GridFileLazyListPreview() {
    val lazyPagingItems = PagingData.flowData<File> { fakeBookFile(it) }.collectAsLazyPagingItems()
    PreviewTheme {
        Scaffold {
            FileLazyVerticalGrid(
                uiState = FileLazyVerticalGridUiState(
                    fileListDisplay = FileListDisplay.List,
                ),
                lazyPagingItems = lazyPagingItems,
                onItemClick = {},
                onItemInfoClick = {},
                contentPadding = it,
            )
        }
    }
}
