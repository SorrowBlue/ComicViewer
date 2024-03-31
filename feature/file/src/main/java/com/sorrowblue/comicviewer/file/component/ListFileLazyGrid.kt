package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

@Composable
fun <T : File> ListFileLazyGrid(
    lazyPagingItems: LazyPagingItems<T>,
    onClickItem: (T) -> Unit,
    onLongClickItem: (T) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    isThumbnailEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val isCompact = LocalWindowSize.current.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        state = state,
        verticalArrangement = if (isCompact) Arrangement.Top else Arrangement.spacedBy(ComicTheme.dimension.padding),
        contentPadding = if (isCompact) {
            contentPadding
        } else {
            contentPadding.add(PaddingValues(ComicTheme.dimension.margin))
        },
        modifier = modifier
            .drawVerticalScrollbar(state, 1),
    ) {
        items(count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.path }) {
            lazyPagingItems[it]?.let { item ->
                if (isCompact) {
                    ListFile(
                        file = item,
                        isThumbnailEnabled = isThumbnailEnabled,
                        onClick = { onClickItem(item) },
                        onLongClick = { onLongClickItem(item) },
                    )
                } else {
                    ListFileCard(
                        file = item,
                        isThumbnailEnabled = isThumbnailEnabled,
                        onClick = { onClickItem(item) },
                        onLongClick = { onLongClickItem(item) }
                    )
                }
            }
        }
    }
}
