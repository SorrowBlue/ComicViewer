package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.layout.animateMainContentPaddingValues
import com.sorrowblue.comicviewer.framework.ui.layout.plus

sealed class LazyPagingColumnType {

    abstract val columns: GridCells

    data object List : LazyPagingColumnType() {
        override val columns = GridCells.Fixed(1)
    }

    data object ListMedium : LazyPagingColumnType() {
        override val columns = GridCells.Adaptive(500.dp)
    }

    data class Grid(val minSize: Int) : LazyPagingColumnType() {
        override val columns = GridCells.Adaptive(minSize.dp)
    }
}

@Composable
fun <T : Any> LazyPagingColumn(
    lazyPagingItems: LazyPagingItems<T>,
    type: LazyPagingColumnType,
    modifier: Modifier = Modifier,
    autoPadding: Boolean = true,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    fillWidth: Boolean = type is LazyPagingColumnType.List,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { type },
    itemContent: @Composable (LazyGridItemScope.(index: Int, item: T) -> Unit),
) {
    val padding = if (autoPadding) {
        animateMainContentPaddingValues(type == LazyPagingColumnType.List).value
    } else {
        PaddingValues()
    }
    LazyVerticalGrid(
        columns = type.columns,
        state = state,
        contentPadding = contentPadding + padding,
        verticalArrangement = if (fillWidth) Arrangement.Top else Arrangement.spacedBy(ComicTheme.dimension.padding),
        horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding),
        modifier = modifier
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = key,
            contentType = contentType
        ) { index ->
            lazyPagingItems[index]?.let {
                itemContent(index, it)
            }
        }
    }
}
