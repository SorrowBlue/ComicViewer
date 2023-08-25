package com.sorrowblue.comicviewer.feature.search.section

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.placeholder.debugPlaceholder
import com.sorrowblue.comicviewer.framework.compose.placeholder.placeholder3

data class SearchResultSheetUiState(
    val isShrink: Boolean = false,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SearchResultSheet(
    lazyPagingItems: LazyPagingItems<File>,
    lazyListState: LazyListState = rememberLazyListState(),
    uiState: SearchResultSheetUiState = SearchResultSheetUiState(),
    onExpandRequest: () -> Unit,
    onFileClick: (File) -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .alpha(if (uiState.isShrink) 1f else 0.75f)
    ) {
        Column(
            Modifier
                .scrollable(rememberScrollableState { it }, Orientation.Vertical)
                .draggable(
                    interactionSource = remember { MutableInteractionSource() },
                    state = remember { DraggableState {} },
                    orientation = Orientation.Vertical,
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .weight(1f, true)
                        .padding(16.dp),
                    text = "999 Result",
                    style = MaterialTheme.typography.titleLarge
                )
                if (uiState.isShrink) {
                    IconButton(onClick = onExpandRequest) {
                        Icon(Icons.TwoTone.KeyboardArrowUp, "")
                    }
                }
            }
            Divider(Modifier.padding(horizontal = AppMaterialTheme.dimens.margin))
            LazyColumn(
                Modifier
                    .fillMaxSize(),
                state = lazyListState
            ) {
                items(
                    lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { "${it.bookshelfId}${it.path}" }
                ) { index ->
                    val file = lazyPagingItems[index]
                    FileList(
                        file,
                        Modifier.combinedClickable(onClick = { onFileClick(file!!) })
                    )
                }
            }
        }
        if (uiState.isShrink) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onExpandRequest)
            )
        }
    }
}

@Composable
fun FileList(file: File?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(start = 16.dp, top = 8.dp, end = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = file,
            contentDescription = "",
            modifier = Modifier
                .size(56.dp)
                .placeholder3(file == null),
            placeholder = debugPlaceholder()
        )
        Spacer(Modifier.size(16.dp))
        Text(
            file?.name.orEmpty(),
            Modifier
                .weight(1f, true)
                .placeholder3(file == null),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.size(16.dp))
        if (file is Book && 0 < file.totalPageCount) {
            Text(
                "${file.totalPageCount}",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
