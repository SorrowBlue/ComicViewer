package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import com.sorrowblue.comicviewer.framework.ui.layout.animateMainContentPaddingValues
import com.sorrowblue.comicviewer.framework.ui.layout.plus

sealed class LazyPagingColumn {
    abstract val columns: GridCells

    data object List : LazyPagingColumn() {
        override val columns = GridCells.Fixed(1)
    }

    data object ListMedium : LazyPagingColumn() {
        override val columns = GridCells.Adaptive(500.dp)
    }

    data class Grid(val minSize: Int) : LazyPagingColumn() {
        override val columns = GridCells.Adaptive(minSize.dp)
    }
    data class FixedGrid(val count: Int) : LazyPagingColumn() {
        override val columns = GridCells.Fixed(count)
    }
}

@Composable
fun <T : Any> LazyPagingColumn(
    lazyPagingItems: LazyPagingItems<T>,
    type: LazyPagingColumn,
    modifier: Modifier = Modifier,
    autoPadding: Boolean = true,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    fillWidth: Boolean = type is LazyPagingColumn.List,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { type },
    itemContent: @Composable (LazyGridItemScope.(index: Int, item: T) -> Unit),
) {
    val padding = if (autoPadding) {
        animateMainContentPaddingValues(type == LazyPagingColumn.List).value
    } else {
        PaddingValues()
    }
    val scrollAreaState = rememberScrollAreaState(state)
    ScrollArea(state = scrollAreaState, modifier = modifier) {
        LazyVerticalGrid(
            columns = type.columns,
            state = state,
            contentPadding = contentPadding.plus(padding),
            verticalArrangement = if (fillWidth) {
                Arrangement.Top
            } else {
                Arrangement.spacedBy(16.dp)
            },
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = key,
                contentType = contentType,
            ) { index ->
                lazyPagingItems[index]?.let {
                    itemContent(index, it)
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.TopEnd)
                .fillMaxHeight()
                .width(24.dp)
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding(),
                ).windowInsetsPadding(WindowInsets.safeGestures.only(WindowInsetsSides.End)),
        ) {
            Thumb(
                modifier = Modifier.background(Color.Black.copy(0.78f), RoundedCornerShape(100)),
            )
        }
    }
}
