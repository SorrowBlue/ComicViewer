package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.component.BookshelfSource
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Text
import kotlinx.collections.immutable.toPersistentList

@Composable
fun BookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
) {
    com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog(
        onDismissRequest = onBackClick,
        title = { Text(id = R.string.bookshelf_selection_title) }
    ) {
        val list = remember { BookshelfType.entries.toPersistentList() }

        LazyColumn(
            contentPadding = it,
            verticalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding * 2),
        ) {
            items(list) { type ->
                BookshelfSource(
                    type = type,
                    onClick = { onSourceClick(type) }
                )
            }
        }
    }
}
