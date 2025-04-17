package com.sorrowblue.comicviewer.feature.bookshelf.info.delete

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.nextLoremIpsum

@PreviewMultiScreen
@Composable
private fun BookshelfDeleteScreenPreview(
    @PreviewParameter(BookshelfDeleteScreenUiStateConfig::class) uiState: BookshelfDeleteScreenUiState,
) {
    PreviewTheme {
        BookshelfDeleteScreen(
            uiState = uiState,
            onDismissRequest = {},
            onDismissClick = {},
            onConfirmClick = {}
        )
    }
}

private class BookshelfDeleteScreenUiStateConfig :
    PreviewParameterProvider<BookshelfDeleteScreenUiState> {
    override val values = sequenceOf(
        BookshelfDeleteScreenUiState(nextLoremIpsum(), false),
        BookshelfDeleteScreenUiState(nextLoremIpsum(), true),
    )
}
