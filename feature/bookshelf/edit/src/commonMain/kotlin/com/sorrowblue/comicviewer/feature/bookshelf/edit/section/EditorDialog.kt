package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
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
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenUiState
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_save
import comicviewer.feature.bookshelf.edit.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form

@Composable
internal fun EditorDialog(
    form: Form<out BookshelfEditForm>,
    uiState: BookshelfEditScreenUiState,
    onDismissRequest: () -> Unit,
    scrollState: ScrollState,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = form::handleSubmit,
                enabled = form.meta.canSubmit
            ) {
                AnimatedContent(
                    targetState = form.meta.canSubmit,
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
            TextButton(onClick = onDismissRequest, enabled = form.meta.canSubmit) {
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
        properties = if (form.meta.canSubmit) {
            DialogProperties()
        } else {
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        }
    )
}
