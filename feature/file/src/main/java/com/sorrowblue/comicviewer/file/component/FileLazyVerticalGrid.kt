package com.sorrowblue.comicviewer.file.component

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    var spanCount by remember { mutableIntStateOf(1) }
    val contentType by rememberFileContentType(
        fileListDisplay = uiState.fileListDisplay,
        gridColumnSize = uiState.columnSize
    )
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
    LazyVerticalGrid(
        columns = contentType.columns,
        state = state,
        contentPadding = when (contentType) {
            FileContentType.List -> contentPadding
            FileContentType.ListMedium -> contentPadding.add(PaddingValues(ComicTheme.dimension.margin))
            is FileContentType.Grid -> contentPadding.add(PaddingValues(ComicTheme.dimension.margin))
        },
        verticalArrangement = when (contentType) {
            FileContentType.List -> Arrangement.Top
            FileContentType.ListMedium ->
                Arrangement.spacedBy(ComicTheme.dimension.padding, Alignment.Top)

            is FileContentType.Grid ->
                Arrangement.spacedBy(ComicTheme.dimension.padding, Alignment.Top)
        },
        horizontalArrangement = when (contentType) {
            FileContentType.List -> Arrangement.Start
            FileContentType.ListMedium -> Arrangement.Start
            is FileContentType.Grid ->
                Arrangement.spacedBy(ComicTheme.dimension.padding, Alignment.Start)
        },
        modifier = modifier
            .drawVerticalScrollbar(state, spanCount),
    ) {
        items(
            count = lazyPagingItems.itemCount,
            span = {
                spanCount = maxLineSpan
                GridItemSpan(1)
            },
            key = lazyPagingItems.itemKey { it.path },
            contentType = { contentType }
        ) {
            lazyPagingItems[it]?.let { item ->
                when (contentType) {
                    FileContentType.List -> {
                        ListFile(
                            file = item,
                            onClick = { onItemClick(item) },
                            onLongClick = { onItemInfoClick(item) },
                            showThumbnail = uiState.showThumbnails,
                            fontSize = uiState.fontSize,
                            contentScale = contentScale,
                            filterQuality = filterQuality
                        )
                    }

                    FileContentType.ListMedium -> {
                        ListFileCard(
                            file = item,
                            onClick = { onItemClick(item) },
                            onLongClick = { onItemInfoClick(item) },
                            showThumbnail = uiState.showThumbnails,
                            fontSize = uiState.fontSize,
                            contentScale = contentScale,
                            filterQuality = filterQuality,
                        )
                    }

                    is FileContentType.Grid -> GridFile(
                        file = item,
                        onClick = { onItemClick(item) },
                        onInfoClick = { onItemInfoClick(item) },
                        showThumbnail = uiState.showThumbnails,
                        fontSize = uiState.fontSize,
                        contentScale = contentScale,
                        filterQuality = filterQuality,
                    )
                }
            }
        }
    }
}

@PreviewMultiScreen
@Composable
private fun PreviewGridFileLazyGrid() {
    val files = List(20) {
        fakeBookFile(it)
    }
    val pagingDataFlow: Flow<PagingData<File>> = flowOf(PagingData.from(files))
    PreviewTheme {
        Scaffold {
            FileLazyVerticalGrid(
                uiState = FileLazyVerticalGridUiState(),
                lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems(),
                onItemClick = {},
                onItemInfoClick = {},
                contentPadding = it
            )
        }
    }
}
