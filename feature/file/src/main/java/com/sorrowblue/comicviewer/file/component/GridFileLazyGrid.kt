package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : File> GridFileLazyGrid(
    lazyPagingItems: LazyPagingItems<T>,
    columns: GridCells,
    onClickItem: (T) -> Unit,
    onLongClickItem: (T) -> Unit,
    modifier: Modifier = Modifier,
    isThumbnailEnabled: Boolean = false,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    var spanCount by remember { mutableIntStateOf(1) }
    val span = remember { GridItemSpan(1) }
    LazyVerticalGrid(
        columns = columns,
        state = state,
        contentPadding = contentPadding.add(PaddingValues(ComicTheme.dimension.margin)),
        verticalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding, Alignment.Top),
        horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding, Alignment.Start),
        modifier = modifier.drawVerticalScrollbar(state, spanCount)
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { "${it.bookshelfId.value}/${it.path}" },
            span = {
                spanCount = maxLineSpan
                span
            }
        ) {
            lazyPagingItems[it]?.let { item ->
                GridFile(
                    file = item,
                    onClick = { onClickItem(item) },
                    onInfoClick = { onLongClickItem(item) },
                    isThumbnailEnabled = isThumbnailEnabled,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}
