package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.InternalStorageEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditorContents
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass

@Composable
context(context: BookshelfEditScreenContext)
fun BookshelfEditorDialog(
    type: BookshelfEditorType,
    onBackClick: () -> Unit,
    discardConfirm: () -> Unit,
    onEditComplete: () -> Unit,
) {
    val isFullScreenDialog = isCompactWindowClass()
    val state = rememberBookshelfEditorScreenState(editorType = type)
    BasicAlertDialog(
        onDismissRequest = {
            if (state.initialForm == state.form.value) {
                onBackClick()
            } else {
                discardConfirm
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = !isFullScreenDialog)
    ) {
        when (state) {
            is InternalStorageEditorScreenState -> InternalStorageEditorContents(
                state = state,
                onBackClick = {
                    if (state.initialForm == state.form.value) {
                        onBackClick()
                    } else {
                        discardConfirm
                    }
                },
            )

            is SmbEditorScreenState -> SmbEditorContents(
                state = state,
                onBackClick = {
                    if (state.initialForm == state.form.value) {
                        onBackClick()
                    } else {
                        discardConfirm
                    }
                },
            )
        }
        EventEffect(state.events) {
            when (it) {
                BookshelfEditScreenEvent.Complete -> onEditComplete()
            }
        }
    }
}
