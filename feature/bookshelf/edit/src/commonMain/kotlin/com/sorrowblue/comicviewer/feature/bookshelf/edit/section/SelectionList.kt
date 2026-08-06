/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.BookshelfSource
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun SelectionList(
    items: List<BookshelfType>,
    onSourceClick: (BookshelfType) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        state = lazyListState,
        contentPadding = contentPadding.plus(PaddingValues(bottom = 16.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .drawDivider(lazyListState, visibleTop = true, visibleBottom = true),
    ) {
        items(items = items) {
            BookshelfSource(
                type = it,
                onClick = { onSourceClick(it) },
                modifier = Modifier.testTag("BookshelfSelectionItem-${it.name}"),
            )
        }
    }
}

@Composable
fun Modifier.drawDivider(
    state: ScrollableState,
    visibleTop: Boolean,
    visibleBottom: Boolean,
): Modifier {
    val topDividerAlpha by animateFloatAsState(
        targetValue = if (visibleTop && state.canScrollBackward) 1f else 0f,
        label = "TopDividerAlpha",
    )
    val bottomDividerAlpha by animateFloatAsState(
        targetValue = if (visibleBottom && state.canScrollForward) 1f else 0f,
        label = "BottomDividerAlpha",
    )
    val dividerColor = ComicTheme.colorScheme.outlineVariant
    return drawWithContent {
        drawContent()
        if (topDividerAlpha > 0f) {
            val strokeWidth = 1.dp.toPx()
            val y = strokeWidth
            drawLine(
                color = dividerColor.copy(alpha = topDividerAlpha),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidth,
            )
        }
        if (bottomDividerAlpha > 0f) {
            val strokeWidth = 1.dp.toPx()
            val y = size.height - strokeWidth
            drawLine(
                color = dividerColor.copy(alpha = bottomDividerAlpha),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidth,
            )
        }
    }
}
