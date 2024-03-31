package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.section.BookshelfSourceList
import com.sorrowblue.comicviewer.framework.ui.material3.BackButton
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

interface BookshelfSelectionScreenNavigator {
    fun navigateUp()
    fun onSourceClick(bookshelfType: BookshelfType)
}

@Destination
@Composable
internal fun BookshelfSelectionScreen(navigator: BookshelfSelectionScreenNavigator) {
    BookshelfSelectionScreen(
        onBackClick = navigator::navigateUp,
        onSourceClick = navigator::onSourceClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    state: BookshelfSelectionScreenState = rememberBookshelfSelectionScreenState(),
) {
    val uiState = state.uiState
    BookshelfSelectionScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSourceClick = onSourceClick
    )
}

internal data class BookshelfSelectionScreenUiState(
    val list: PersistentList<BookshelfType> = BookshelfType.entries.toPersistentList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfSelectionScreen(
    uiState: BookshelfSelectionScreenUiState,
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.bookshelf_selection_title)) },
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
                scrollBehavior = scrollBehavior
            )
        },
    ) { contentPadding ->
        BookshelfSourceList(
            items = uiState.list,
            onSourceClick = onSourceClick,
            state = lazyListState,
            contentPadding = contentPadding
        )
    }
}
