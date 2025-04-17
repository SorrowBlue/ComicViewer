package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewDevice

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
