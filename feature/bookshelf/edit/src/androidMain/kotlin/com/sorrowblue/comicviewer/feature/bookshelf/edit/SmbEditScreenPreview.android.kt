package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm.Auth
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@PreviewMultiScreen
@Composable
private fun SmbEditScreenPreview() {
    PreviewTheme {
        val isCompact = isCompactWindowClass()
        SmbEditScreen(
            isDialog = !isCompact,
            uiState = SmbEditScreenUiState(
                form = SmbEditScreenForm(auth = Auth.UserPass),
                editMode = BookshelfEditMode.Register(BookshelfType.DEVICE)
            ),
            snackbarHostState = SnackbarHostState(),
            onBackClick = {},
            onSubmit = {}
        )
    }
}
