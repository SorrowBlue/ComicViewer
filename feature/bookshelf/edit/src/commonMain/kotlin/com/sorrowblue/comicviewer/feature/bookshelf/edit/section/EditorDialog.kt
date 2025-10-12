package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditorForm
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialogContent
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_save
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_title_edit
import comicviewer.feature.bookshelf.edit.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form

data class BookshelfEditorScreenUiState(
    val progress: Boolean = true,
)

@Composable
internal fun EditorDialog(
    form: Form<out BookshelfEditorForm>,
    uiState: BookshelfEditorScreenUiState,
    onDismissRequest: () -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialogContent(
        title = {
            Text(stringResource(Res.string.bookshelf_edit_title_edit))
        },
        scrollableState = scrollState,
        confirmButton = {
            val dialogTextStyle = LocalTextStyle.current
            TextButton(
                onClick = form::handleSubmit,
                enabled = !uiState.progress,
                colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current)
            ) {
                AnimatedContent(targetState = uiState.progress, label = "progress") {
                    if (it) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.bookshelf_edit_label_save),
                            style = dialogTextStyle
                        )
                    }
                }
            }
        },
        dismissButton = {
            val dialogTextStyle = LocalTextStyle.current
            TextButton(
                onClick = onDismissRequest,
                enabled = !uiState.progress,
                colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current)
            ) {
                Text(text = stringResource(Res.string.cancel), style = dialogTextStyle)
            }
        },
        modifier = modifier,
        content = {
            Column {
                content()
                Spacer(Modifier.padding(bottom = 8.dp))
            }
        }
    )
}
