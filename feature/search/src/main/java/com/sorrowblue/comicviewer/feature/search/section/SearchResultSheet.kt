package com.sorrowblue.comicviewer.feature.search.section

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.feature.search.R
import com.sorrowblue.comicviewer.feature.search.component.FileList
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

@Composable
internal fun SearchResultSheet(
    query: String,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
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
        SearchResultContents(
            lazyListState = lazyListState,
            contentPadding = contentPadding,
            lazyPagingItems = lazyPagingItems,
            onFileClick = onFileClick,
            onFileLongClick = onFileLongClick
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
private fun SearchResultContents() {

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
//        Image(
//            painter = painterResource(id = FrameworkDrawable.ic_undraw_file_searching_re_3evy),
//            contentDescription = null,
//            modifier = Modifier.size(200.dp)
//        )
        Text(
            text = stringResource(id = R.string.search_label_not_found, query),
            modifier = Modifier.width(200.dp)
        )
    }
}
