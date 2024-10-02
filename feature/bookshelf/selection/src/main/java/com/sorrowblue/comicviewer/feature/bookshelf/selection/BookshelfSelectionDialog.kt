package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.section.BookshelfSourceList
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
internal fun BookshelfSelectionDialog(
    onDismissRequest: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    state: BookshelfSelectionScreenState = rememberBookshelfSelectionScreenState(),
) {
    BookshelfSelectionDialog(
        uiState = state.uiState,
        onDismissRequest = onDismissRequest,
        onSourceClick = onSourceClick
    )
}

@Composable
private fun BookshelfSelectionDialog(
    uiState: BookshelfSelectionScreenUiState,
    onDismissRequest: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            shape = AlertDialogDefaults.shape
        ) {
            Column(modifier = Modifier) {
                val mergedStyle = LocalTextStyle.current.merge(ComicTheme.typography.headlineSmall)
                CompositionLocalProvider(
                    LocalContentColor provides AlertDialogDefaults.titleContentColor,
                    LocalTextStyle provides mergedStyle,
                ) {
                    Box(
                        Modifier
                            .padding(
                                PaddingValues(
                                    start = 24.dp,
                                    top = 24.dp,
                                    end = 24.dp,
                                    bottom = 16.dp
                                )
                            )
                            .align(Alignment.Start)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(id = R.string.bookshelf_selection_title))
                            Spacer(modifier = Modifier.weight(1f))
                            CloseIconButton(onClick = onDismissRequest)
                        }
                    }
                }
                if (lazyListState.canScrollBackward) {
                    HorizontalDivider()
                }
                BookshelfSourceList(
                    items = uiState.list,
                    onSourceClick = onSourceClick,
                    state = lazyListState,
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun BookshelfSelectionDialogPreview() {
    PreviewTheme {
        BookshelfSelectionDialog(
            uiState = BookshelfSelectionScreenUiState(),
            onDismissRequest = {},
            onSourceClick = {}
        )
    }
}
