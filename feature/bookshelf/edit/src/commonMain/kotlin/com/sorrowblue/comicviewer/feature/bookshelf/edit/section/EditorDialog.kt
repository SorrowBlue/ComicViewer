package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenUiState
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_save
import comicviewer.feature.bookshelf.edit.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource
import soil.form.Submission

@Composable
internal fun EditorDialog(
    uiState: BookshelfEditScreenUiState,
    onDismissRequest: () -> Unit,
    submission: Submission,
    scrollState: ScrollState,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = submission.onSubmit,
                enabled = !submission.isSubmitting
            ) {
                AnimatedContent(
                    targetState = !submission.isSubmitting,
                    label = "progress"
                ) {
                    if (it) {
                        Text(text = stringResource(Res.string.bookshelf_edit_label_save))
                    } else {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest, enabled = !submission.isSubmitting) {
                Text(text = stringResource(Res.string.cancel))
            }
        },
        title = {
            Text(text = uiState.editMode.title)
        },
        text = {
            Column {
                if (scrollState.canScrollBackward) {
                    HorizontalDivider()
                }
                Column(Modifier.verticalScroll(scrollState)) {
                    content(this)
                }
                if (scrollState.canScrollForward) {
                    HorizontalDivider()
                }
            }
        },
        properties = if (submission.isSubmitting) {
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        } else {
            DialogProperties()
        }
    )
}
