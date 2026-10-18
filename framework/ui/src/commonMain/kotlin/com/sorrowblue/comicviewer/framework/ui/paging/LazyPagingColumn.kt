/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.composeunstyled.Thumb
import com.composeunstyled.ThumbVisibility
import com.composeunstyled.UnstyledVerticalScrollbar
import com.composeunstyled.rememberScrollbarState
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import kotlin.time.Duration.Companion.seconds

sealed interface LazyPagingColumn {
    val columns: GridCells
    val cacheWindow: LazyLayoutCacheWindow

    data object List : LazyPagingColumn {
        override val columns = GridCells.Fixed(1)
        override val cacheWindow = LazyLayoutCacheWindow(ahead = 320.dp, behind = 200.dp)
    }

    data object ListMedium : LazyPagingColumn {
        override val columns = GridCells.Adaptive(500.dp)
        override val cacheWindow = LazyLayoutCacheWindow(ahead = 360.dp, behind = 240.dp)
    }

    data class Grid(val minSize: Int) : LazyPagingColumn {
        override val columns = GridCells.Adaptive(minSize.dp)
        override val cacheWindow = LazyLayoutCacheWindow(ahead = 500.dp, behind = 360.dp)
    }

    data class FixedGrid(val count: Int) : LazyPagingColumn {
        override val columns = GridCells.Fixed(count)
        override val cacheWindow = LazyLayoutCacheWindow(ahead = 480.dp, behind = 320.dp)
    }
}

@Composable
fun <T : Any> LazyPagingColumn(
    lazyPagingItems: LazyPagingItems<T>,
    type: LazyPagingColumn,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    cacheWindow: LazyLayoutCacheWindow = type.cacheWindow,
    contentPadding: PaddingValues = PaddingValues(),
    fillWidth: Boolean = type is LazyPagingColumn.List,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { type },
    itemContent: @Composable (LazyGridItemScope.(index: Int, item: T) -> Unit),
) {
    val scrollbarState = rememberScrollbarState(state)
    Box(modifier = modifier) {
        LazyVerticalGrid(
            columns = type.columns,
            state = state,
            cacheWindow = cacheWindow,
            contentPadding = contentPadding,
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
        UnstyledVerticalScrollbar(
            scrollbarState = scrollbarState,
            modifier = Modifier.align(Alignment.TopEnd)
                .fillMaxHeight()
                .padding(contentPadding.only(PaddingValuesSides.Vertical))
                .windowInsetsPadding(WindowInsets.safeGestures.only(WindowInsetsSides.End))
                .width(16.dp),
        ) {
            Thumb(
                thumbVisibility = ThumbVisibility.HideWhileIdle(fadeIn(), fadeOut(), 1.5.seconds),
                modifier = Modifier.background(
                    LocalContentColor.current.copy(0.25f),
                    RoundedCornerShape(100),
                ).fillMaxWidth(),
            )
        }
    }
}
