package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.section.BookshelfSourceList
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewDevice

interface BookshelfSelectionScreenNavigator {
    fun navigateUp()
    fun onSourceClick(bookshelfType: BookshelfType)
}

@Destination<ExternalModuleGraph>
@Composable
internal fun BookshelfSelectionScreen(navigator: BookshelfSelectionScreenNavigator) {
    if (isCompactWindowClass()) {
        BookshelfSelectionScreen(
            onBackClick = navigator::navigateUp,
            onSourceClick = navigator::onSourceClick,
        )
    } else {
        BookshelfSelectionDialog(
            onDismissRequest = navigator::navigateUp,
            onSourceClick = navigator::onSourceClick
        )
    }
}

@Composable
private fun BookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    state: BookshelfSelectionScreenState = rememberBookshelfSelectionScreenState(),
) {
    BookshelfSelectionScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onSourceClick = onSourceClick
    )
}

internal data class BookshelfSelectionScreenUiState(
    val list: List<BookshelfType> = BookshelfType.entries,
)

@Composable
private fun BookshelfSelectionScreen(
    uiState: BookshelfSelectionScreenUiState,
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.bookshelf_selection_title)) },
                navigationIcon = {
                    BackIconButton(onClick = onBackClick)
                },
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.add(
            ComicTheme.dimension.run {
                WindowInsets(left = margin, right = margin, bottom = margin)
            }
        ),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        BookshelfSourceList(
            items = uiState.list,
            onSourceClick = onSourceClick,
            state = lazyListState,
            contentPadding = contentPadding
        )
    }
}

@Preview
@Composable
private fun BookshelfSelectionScreenPreview() {
    PreviewDevice {
        BookshelfSelectionScreen(
            uiState = BookshelfSelectionScreenUiState(),
            onBackClick = {},
            onSourceClick = {}
        )
    }
}
