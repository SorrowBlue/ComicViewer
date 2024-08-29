package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.section.BookshelfSourceList
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo
import com.sorrowblue.comicviewer.framework.ui.material3.BackButton
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

interface BookshelfSelectionScreenNavigator {
    fun navigateUp()
    fun onSourceClick(bookshelfType: BookshelfType)
}

@Destination<ExternalModuleGraph>
@Composable
internal fun BookshelfSelectionScreen(navigator: BookshelfSelectionScreenNavigator) {
    val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
    if (windowAdaptiveInfo.windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT || windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
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

@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun BookshelfSelectionScreenPreview() {
    PreviewTheme {
        BookshelfSelectionScreen(
            uiState = BookshelfSelectionScreenUiState(),
            onBackClick = {},
            onSourceClick = {}
        )
    }
}
