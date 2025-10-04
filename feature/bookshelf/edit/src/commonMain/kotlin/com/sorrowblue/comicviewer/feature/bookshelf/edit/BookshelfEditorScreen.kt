package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.InternalStorageEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditorContents
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass

@Composable
internal fun BookshelfEditorScreen(
    type: BookshelfEditorType,
    onNavigateUp: (Boolean) -> Unit,
    onEditComplete: () -> Unit,
) {
    val isFullScreenDialog = isCompactWindowClass()
    val state = rememberBookshelfEditorScreenState(editorType = type)
    BasicAlertDialog(
        onDismissRequest = {
            onNavigateUp(state.initialForm != state.form.value)
        },
        properties = DialogProperties(usePlatformDefaultWidth = !isFullScreenDialog)
    ) {
        when (state) {
            is InternalStorageEditorScreenState -> InternalStorageEditorContents(
                state = state,
                onBackClick = { onNavigateUp(state.initialForm != state.form.value) },
            )

            is SmbEditorScreenState -> SmbEditorContents(
                state = state,
                onBackClick = { onNavigateUp(state.initialForm != state.form.value) },
            )
        }
        EventEffect(state.events) {
            when (it) {
                BookshelfEditScreenEvent.Complete -> onEditComplete()
            }
        }
    }
}
