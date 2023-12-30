package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.component.BookshelfSource
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookshelfSelectionScreen(
    onCloseClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    contentPadding: PaddingValues,
    state: BookshelfSelectionScreenState = rememberBookshelfSelectionScreenState(),
) {
    val uiState = state.uiState
    BookshelfSelectionScreen(
        uiState = uiState,
        onCloseClick = onCloseClick,
        onSourceClick = onSourceClick,
        contentPadding = contentPadding,
        windowSizeClass = state.windowSizeClass,
        scrollBehavior = state.scrollBehavior,
    )
}

internal data class BookshelfSelectionScreenUiState(
    val list: PersistentList<BookshelfType> = BookshelfType.entries.toPersistentList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfSelectionScreen(
    uiState: BookshelfSelectionScreenUiState,
    onCloseClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    contentPadding: PaddingValues,
    windowSizeClass: WindowSizeClass,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact || windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.bookshelf_selection_title)) },
                    navigationIcon = {
                        IconButton(onClick = onCloseClick) {
                            Icon(
                                imageVector = ComicIcons.Close,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            contentWindowInsets = contentPadding.asWindowInsets(),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            LazyVerticalGrid(
                contentPadding = it.add(
                    paddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                ),
                columns = GridCells.Adaptive(300.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.list) { type ->
                    BookshelfSource(
                        type = type,
                        onClick = { onSourceClick(type) }
                    )
                }
            }
        }
    } else {
        AlertDialog(
            onDismissRequest = onCloseClick,
            confirmButton = {
                TextButton(onClick = onCloseClick) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
            title = {
                Row {
                    Text(text = stringResource(id = R.string.bookshelf_selection_title))
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onCloseClick) {
                        Icon(imageVector = ComicIcons.Close, contentDescription = null)
                    }
                }
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(400.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.list) { type ->
                        BookshelfSource(
                            type = type,
                            onClick = { onSourceClick(type) }
                        )
                    }
                }
            }
        )
    }
}
