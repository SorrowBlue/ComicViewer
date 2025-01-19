package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

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
