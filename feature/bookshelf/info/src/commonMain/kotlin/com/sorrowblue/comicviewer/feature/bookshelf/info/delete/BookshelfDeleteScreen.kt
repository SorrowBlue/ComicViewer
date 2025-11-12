package com.sorrowblue.comicviewer.feature.bookshelf.info.delete

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_delete_btn_cancel
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_delete_btn_delete
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_delete_text
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_delete_text_null
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_delete_title
import org.jetbrains.compose.resources.stringResource

internal data class BookshelfDeleteScreenUiState(
    val title: String? = null,
    val isProcessing: Boolean = false,
)

@Composable
context(context: BookshelfDeleteScreenContext)
fun BookshelfDeleteScreen(
    bookshelfId: BookshelfId,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val state = rememberBookshelfDeleteScreenState(
        bookshelfId = bookshelfId,
        getBookshelfInfoUseCase = context.getBookshelfInfoUseCase,
        updateDeletionFlagUseCase = context.updateDeletionFlagUseCase,
    )
    BookshelfDeleteScreen(
        uiState = state.uiState,
        onDismissRequest = onBackClick,
        onDismissClick = onBackClick,
        onConfirmClick = { state.onConfirmClick(onComplete) },
    )
}

@Composable
internal fun BookshelfDeleteScreen(
    uiState: BookshelfDeleteScreenUiState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(Res.string.bookshelf_info_delete_title))
        },
        text = {
            val text = uiState.title?.let {
                stringResource(Res.string.bookshelf_info_delete_text, uiState.title)
            } ?: stringResource(Res.string.bookshelf_info_delete_text_null)
            Text(text = text)
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick,
                enabled = !uiState.isProcessing,
                contentPadding = if (uiState.isProcessing) ButtonDefaults.TextButtonWithIconContentPadding else ButtonDefaults.TextButtonContentPadding,
            ) {
                AnimatedVisibility(uiState.isProcessing, label = "progress") {
                    Row {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            color = ButtonDefaults.textButtonColors().disabledContentColor,
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    }
                }
                Text(stringResource(Res.string.bookshelf_info_delete_btn_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClick,
                enabled = !uiState.isProcessing,
            ) {
                Text(stringResource(Res.string.bookshelf_info_delete_btn_cancel))
            }
        },
        properties = if (uiState.isProcessing) {
            DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
        } else {
            DialogProperties()
        },
    )
}
