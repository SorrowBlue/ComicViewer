package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.BookshelfSource

@Composable
internal fun SelectionList(
    items: List<BookshelfType>,
    onSourceClick: (BookshelfType) -> Unit,
    state: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        items(items = items) {
            BookshelfSource(type = it, onClick = { onSourceClick(it) })
        }
    }
}
