package com.sorrowblue.comicviewer.feature.bookshelf.selection.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.component.BookshelfSource
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun BookshelfSourceList(
    items: PersistentList<BookshelfType>,
    onSourceClick: (BookshelfType) -> Unit,
    state: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.drawVerticalScrollbar(state)
    ) {
        items(items = items) {
            BookshelfSource(type = it, onClick = { onSourceClick(it) })
        }
    }
}
