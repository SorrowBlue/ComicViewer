package com.sorrowblue.comicviewer.file.component

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.fakeBookFile
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T : File> FileLazyVerticalGrid(
    contentType: FileContentType2,
    lazyPagingItems: LazyPagingItems<T>,
    onItemClick: (T) -> Unit,
    onItemInfoClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    isThumbnailEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(),
) {
    var spanCount by remember { mutableIntStateOf(1) }
    LazyVerticalGrid(
        columns = contentType.columns,
        state = state,
        contentPadding = when (contentType) {
            FileContentType2.List -> contentPadding
            FileContentType2.ListMedium -> contentPadding.add(PaddingValues(ComicTheme.dimension.margin))
            is FileContentType2.Grid -> contentPadding.add(PaddingValues(ComicTheme.dimension.margin))
        },
        verticalArrangement = when (contentType) {
            FileContentType2.List -> Arrangement.Top
            FileContentType2.ListMedium ->
                Arrangement.spacedBy(ComicTheme.dimension.padding, Alignment.Top)

            is FileContentType2.Grid ->
                Arrangement.spacedBy(ComicTheme.dimension.padding, Alignment.Top)
        },
        horizontalArrangement = when (contentType) {
            FileContentType2.List -> Arrangement.Start
            FileContentType2.ListMedium -> Arrangement.Start
            is FileContentType2.Grid ->
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
                    FileContentType2.List -> {
                        ListFile(
                            file = item,
                            isThumbnailEnabled = isThumbnailEnabled,
                            onClick = { onItemClick(item) },
                            onLongClick = { onItemInfoClick(item) },
                        )
                    }

                    FileContentType2.ListMedium -> {
                        ListFileCard(
                            file = item,
                            isThumbnailEnabled = isThumbnailEnabled,
                            onClick = { onItemClick(item) },
                            onLongClick = { onItemInfoClick(item) }
                        )
                    }

                    is FileContentType2.Grid -> GridFile(
                        file = item,
                        onClick = { onItemClick(item) },
                        onInfoClick = { onItemInfoClick(item) },
                        modifier = Modifier.animateItem(),
                        isThumbnailEnabled = isThumbnailEnabled,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewGridFileLazyGrid(
    @PreviewParameter(FileContentType2PreviewParameterProvider::class) fileContentType2: FileContentType2,
) {
    val files = List(20) {
        fakeBookFile(BookshelfId(it))
    }
    val pagingDataFlow: Flow<PagingData<File>> = flowOf(PagingData.from(files))
    PreviewTheme {
        Scaffold {
            FileLazyVerticalGrid(
                contentType = fileContentType2,
                lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems(),
                onItemClick = {},
                onItemInfoClick = {},
                contentPadding = it
            )
        }
    }
}

class FileContentType2PreviewParameterProvider : PreviewParameterProvider<FileContentType2> {
    override val values: Sequence<FileContentType2>
        get() = sequenceOf(
            FileContentType2.List,
            FileContentType2.ListMedium,
            FileContentType2.Grid(180),
            FileContentType2.Grid(200)
        )
}
