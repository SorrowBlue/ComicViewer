package com.sorrowblue.comicviewer.feature.bookshelf.remove

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.info.R
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

data class BookshelfRemoveDialogArgs(
    val bookshelfId: BookshelfId,
)

@Destination<ExternalModuleGraph>(
    style = DestinationStyle.Dialog::class,
    navArgs = BookshelfRemoveDialogArgs::class
)
@Composable
internal fun BookshelfRemoveDialog(
    navArgs: BookshelfRemoveDialogArgs,
    destinationsNavigator: ResultBackNavigator<Boolean>,
    state: BookshelfRemoveDialogState = rememberBookshelfRemoveDialogState(navArgs = navArgs),
) {
    BookshelfRemoveDialog(
        uiState = state.uiState,
        onDismissRequest = destinationsNavigator::navigateBack,
        onDismissClick = { destinationsNavigator.navigateBack(false) },
        onConfirmClick = state::remove
    )
    LaunchedEventEffect(state.event) {
        when (it) {
            BookshelfRemoveDialogEvent.RemoveSuccess -> destinationsNavigator.navigateBack(true)
        }
    }
}

internal data class BookshelfRemoveDialogUiState(
    val title: String = "",
    val isProcessing: Boolean = false,
)

@Composable
private fun BookshelfRemoveDialog(
    uiState: BookshelfRemoveDialogUiState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.bookshelf_info_title_remove))
        },
        text = {
            Text(text = stringResource(id = R.string.bookshelf_info_text_remove, uiState.title))
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick, enabled = !uiState.isProcessing) {
                AnimatedContent(uiState.isProcessing, label = "progress") {
                    if (it) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                    } else {
                        Text(stringResource(id = R.string.bookshelf_info_btn_remove))
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClick,
                enabled = !uiState.isProcessing
            ) {
                Text(stringResource(id = android.R.string.cancel))
            }
        },
        properties = if (uiState.isProcessing) {
            DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
        } else {
            DialogProperties()
        }
    )
}

@PreviewMultiScreen
@Composable
private fun BookshelfRemoveDialogPreview() {
    PreviewTheme {
        BookshelfRemoveDialog(
            uiState = BookshelfRemoveDialogUiState(),
            onDismissRequest = {},
            onDismissClick = {},
            onConfirmClick = {}
        )
    }
}
