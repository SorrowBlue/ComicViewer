package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditorForm
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import soil.form.compose.Form

@Composable
internal fun EditorContents(
    form: Form<out BookshelfEditorForm>,
    uiState: BookshelfEditorScreenUiState,
    onBackClick: () -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = isCompactWindowClass(),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (isFullScreen) {
        EditorScreen(
            form = form,
            uiState = uiState,
            onBackClick = onBackClick,
            scrollState = scrollState,
            content = content,
            modifier = modifier,
        )
    } else {
        EditorDialog(
            form = form,
            uiState = uiState,
            onDismissRequest = onBackClick,
            scrollState = scrollState,
            content = content,
            modifier = modifier,
        )
    }
}
