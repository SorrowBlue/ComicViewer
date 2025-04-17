package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@PreviewMultiScreen
@Composable
private fun InternalStorageEditScreenPreview() {
    PreviewTheme {
        val isCompact = isCompactWindowClass()
        InternalStorageEditDialogScreen(
            isDialog = !isCompact,
            uiState = InternalStorageEditScreenUiState(
                form = InternalStorageEditScreenForm(),
                editMode = BookshelfEditMode.Register(BookshelfType.DEVICE)
            ),
            snackbarHostState = SnackbarHostState(),
            onBackClick = {},
            onSubmit = {}
        )
    }
}
