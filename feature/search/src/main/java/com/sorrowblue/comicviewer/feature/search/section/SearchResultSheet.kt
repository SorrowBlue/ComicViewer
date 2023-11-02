package com.sorrowblue.comicviewer.feature.search.section

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.search.R
import com.sorrowblue.comicviewer.feature.search.component.FileList
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.component.FileListContent
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFileSearching
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile

@Composable
internal fun SearchResultSheet(
    query: String,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyGridState = rememberLazyGridState(),
    onFileClick: (File) -> Unit = {},
    onFileLongClick: (File) -> Unit = {},
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContents(
            query = query,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .imePadding()
        )
    } else {
        FileListContent(
            state = lazyListState,
            contentPadding = contentPadding.add(paddingValues = PaddingValues(
                start = if (rememberMobile()) 0.dp else ComicTheme.dimension.margin,
                top = 8.dp,
                end = if (rememberMobile()) 0.dp else ComicTheme.dimension.margin,
                bottom = if (rememberMobile()) 0.dp else ComicTheme.dimension.margin,
            )),
            lazyPagingItems = lazyPagingItems,
            onClickItem = onFileClick,
            onLongClickItem = onFileLongClick
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun SearchResultContents(
    lazyListState: LazyListState,
    contentPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<File>,
    onFileClick: (File) -> Unit,
    onFileLongClick: (File) -> Unit,
) {
    LazyColumn(
        Modifier.imePadding(),
        contentPadding = contentPadding,
        state = lazyListState
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { "${it.bookshelfId}${it.path}" }
        ) { index ->
            val file = lazyPagingItems[index]
            FileList(
                file,
                Modifier
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = { onFileClick(file!!) },
                        onLongClick = { onFileLongClick(file!!) }
                    )
            )
        }
    }
}

@Composable
private fun EmptyContents(
    query: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ComicIcons.UndrawFileSearching,
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = stringResource(id = R.string.search_label_not_found, query),
            modifier = Modifier.width(200.dp)
        )
    }
}
