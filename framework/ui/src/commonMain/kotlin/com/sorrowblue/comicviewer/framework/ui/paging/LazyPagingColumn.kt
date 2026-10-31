/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import com.composeunstyled.Thumb
import com.composeunstyled.ThumbVisibility
import com.composeunstyled.UnstyledVerticalScrollbar
import com.composeunstyled.rememberScrollbarState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
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
    contentType: (index: Int) -> Any? = lazyPagingItems.itemContentType { type },
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
                val item = lazyPagingItems[index]
                if (item != null) {
                    itemContent(index, item)
                } else {
                    LazyPagingColumnPlaceholder(type = type)
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

@Composable
private fun LazyPagingColumnPlaceholder(type: LazyPagingColumn) {
    when (type) {
        LazyPagingColumn.List -> {
            ListPlaceholderItem()
        }

        LazyPagingColumn.ListMedium -> {
            Card(colors = CardDefaults.cardColors()) {
                ListPlaceholderItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }

        is LazyPagingColumn.Grid, is LazyPagingColumn.FixedGrid -> {
            GridPlaceholderItem()
        }
    }
}

@Composable
private fun ListPlaceholderItem(colors: ListItemColors = ListItemDefaults.colors()) {
    val skeletonColor = ComicTheme.colorScheme.onSurfaceVariant.copy(alpha = SkeletonAlpha)
    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(ListThumbnailSize)
                    .clip(CardDefaults.shape)
                    .background(skeletonColor),
            )
        },
        colors = colors,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(ListHeadlineWidthFraction)
                .height(SkeletonTextHeight)
                .clip(SkeletonTextShape)
                .background(skeletonColor),
        )
    }
}

@Composable
private fun GridPlaceholderItem() {
    val skeletonColor = ComicTheme.colorScheme.onSurfaceVariant.copy(alpha = SkeletonAlpha)
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(GridPlaceholderAspectRatio)
                .clip(CardDefaults.shape)
                .background(skeletonColor),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(GridPlaceholderBottomHeight)
                .padding(ComicTheme.dimension.padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(GridTitleLine1WidthFraction)
                    .height(SkeletonTextHeight)
                    .clip(SkeletonTextShape)
                    .background(skeletonColor),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(GridTitleLine2WidthFraction)
                    .height(SkeletonTextHeight)
                    .clip(SkeletonTextShape)
                    .background(skeletonColor),
            )
        }
    }
}

private val ListThumbnailSize = 80.dp
private val SkeletonTextHeight = 16.dp
private val SkeletonTextShape = RoundedCornerShape(4.dp)
private val GridPlaceholderBottomHeight = 72.dp
private const val GridPlaceholderAspectRatio = 3f / 4f
private const val SkeletonAlpha = 0.12f
private const val ListHeadlineWidthFraction = 0.6f
private const val GridTitleLine1WidthFraction = 0.85f
private const val GridTitleLine2WidthFraction = 0.55f
