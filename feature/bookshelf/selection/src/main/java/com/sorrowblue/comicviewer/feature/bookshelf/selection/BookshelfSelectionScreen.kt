package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.component.BookshelfSourceRow
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.bookshelf_selection_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(ComicTheme.dimension.spacer)
        ) {
            items(BookshelfType.values()) { type ->
                BookshelfSourceRow(
                    modifier = Modifier.padding(horizontal = ComicTheme.dimension.margin),
                    type = type,
                    onClick = { onSourceClick(type) }
                )
            }
        }
    }
}
