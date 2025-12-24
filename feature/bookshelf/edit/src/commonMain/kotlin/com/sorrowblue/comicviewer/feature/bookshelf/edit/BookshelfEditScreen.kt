package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.InternalStorageEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditorContents
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass

@Composable
internal fun BookshelfEditScreen(
    state: BookshelfEditScreenState,
    onBackClick: () -> Unit,
    discardConfirm: () -> Unit,
    onEditComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFullScreenDialog = isCompactWindowClass()
    BasicAlertDialog(
        onDismissRequest = {
            if (state.initialForm == state.form.value) {
                onBackClick()
            } else {
                discardConfirm()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = !isFullScreenDialog),
        modifier = modifier,
    ) {
        when (state) {
            is InternalStorageEditScreenState -> InternalStorageEditorContents(
                state = state,
                onBackClick = {
                    if (state.initialForm == state.form.value) {
                        onBackClick()
                    } else {
                        discardConfirm()
                    }
                },
            )

            is SmbEditScreenState -> SmbEditorContents(
                state = state,
                onBackClick = {
                    if (state.initialForm == state.form.value) {
                        onBackClick()
                    } else {
                        discardConfirm()
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
