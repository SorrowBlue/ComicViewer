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
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect

data class BookshelfRemoveDialogArgs(
    val bookshelf: Bookshelf,
)

internal data class BookshelfRemoveDialogUiState(
    val title: String = "",
    val isProcessing: Boolean = false,
)

@Destination<ExternalModuleGraph>(
    navArgs = BookshelfRemoveDialogArgs::class,
    style = DestinationStyle.Dialog::class
)
@Composable
internal fun BookshelfRemoveDialog(
    navArgs: BookshelfRemoveDialogArgs,
    destinationsNavigator: ResultBackNavigator<Boolean>,
    state: BookshelfRemoveDialogState = rememberBookshelfRemoveDialogState(navArgs = navArgs),
) {
    val uiState = state.uiState
    AlertDialog(
        onDismissRequest = destinationsNavigator::navigateBack,
        title = {
            Text(text = stringResource(id = R.string.bookshelf_remove_title))
        },
        text = {
            Text(text = stringResource(id = R.string.bookshelf_remove_message, uiState.title))
        },
        confirmButton = {
            TextButton(onClick = state::remove, enabled = !uiState.isProcessing) {
                AnimatedContent(uiState.isProcessing, label = "progress") {
                    if (it) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                    } else {
                        Text(stringResource(id = R.string.bookshelf_remove_btn_remove))
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { destinationsNavigator.navigateBack(false) },
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

    LaunchedEventEffect(state.event) {
        when (it) {
            BookshelfRemoveDialogEvent.RemoveSuccess -> destinationsNavigator.navigateBack(true)
        }
    }
}
