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
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.section.BookshelfSourceList
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import comicviewer.feature.bookshelf.selection.generated.resources.Res
import comicviewer.feature.bookshelf.selection.generated.resources.bookshelf_selection_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
data object BookshelfSelection

interface BookshelfSelectionNavigator {
    fun navigateUp()
    fun onSourceClick(bookshelfType: BookshelfType)
}

internal data class BookshelfSelectionScreenUiState(
    val list: List<BookshelfType> = BookshelfType.entries,
)

@Destination<BookshelfSelection>(style = DestinationStyle.Auto::class)
@Composable
internal fun BookshelfSelectionScreen(
    navigator: BookshelfSelectionNavigator = koinInject(),
    state: BookshelfSelectionScreenState = rememberBookshelfSelectionScreenState(),
) {
    val uiState = state.uiState
    if (isCompactWindowClass()) {
        BookshelfSelectionScreen(
            uiState = uiState,
            onBackClick = navigator::navigateUp,
            onSourceClick = navigator::onSourceClick,
        )
    } else {
        BookshelfSelectionDialog(
            uiState = uiState,
            onDismissRequest = navigator::navigateUp,
            onSourceClick = navigator::onSourceClick
        )
    }
}

@Composable
internal fun BookshelfSelectionScreen(
    uiState: BookshelfSelectionScreenUiState,
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.bookshelf_selection_title)) },
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
