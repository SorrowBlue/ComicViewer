package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@PreviewMultiScreen
@Composable
private fun BookshelfEditLoadingScreenPreview() {
    PreviewTheme {
        val isCompact = isCompactWindowClass()
        BookshelfEditLoadingScreen(
            isDialog = !isCompact,
            uiState = BookshelfEditScreenUiState.Loading(
                BookshelfEditMode.Register(
                    BookshelfType.DEVICE
                )
            ),
            onBackClick = {}
        )
    }
}
