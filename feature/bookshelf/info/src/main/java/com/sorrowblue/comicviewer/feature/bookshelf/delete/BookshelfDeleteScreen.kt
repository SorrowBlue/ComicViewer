package com.sorrowblue.comicviewer.feature.bookshelf.delete

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
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.nextLoremIpsum

data class BookshelfDeleteScreenArgs(
    val bookshelfId: BookshelfId,
)

@Destination<ExternalModuleGraph>(
    style = DestinationStyle.Dialog::class,
    navArgs = BookshelfDeleteScreenArgs::class
)
@Composable
internal fun BookshelfDeleteScreen(
    navArgs: BookshelfDeleteScreenArgs,
    destinationsNavigator: ResultBackNavigator<Boolean>,
    state: BookshelfDeleteScreenState = rememberBookshelfDeleteScreenState(navArgs = navArgs),
) {
    BookshelfDeleteScreen(
        uiState = state.uiState,
        onDismissRequest = destinationsNavigator::navigateBack,
        onDismissClick = { destinationsNavigator.navigateBack(false) },
        onConfirmClick = state::onConfirmClick
    )
    EventEffect(state.events) {
        when (it) {
            BookshelfDeleteScreenEvent.RemoveSuccess -> destinationsNavigator.navigateBack(true)
        }
    }
}

internal data class BookshelfDeleteScreenUiState(
    val title: String = "",
    val isProcessing: Boolean = false,
)

@Composable
private fun BookshelfDeleteScreen(
    uiState: BookshelfDeleteScreenUiState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.bookshelf_info_delete_title))
        },
        text = {
            Text(text = stringResource(id = R.string.bookshelf_info_delete_text, uiState.title))
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
                        Text(stringResource(id = R.string.bookshelf_info_delete_btn_delete))
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
private fun BookshelfDeleteScreenPreview() {
    PreviewTheme {
        BookshelfDeleteScreen(
            uiState = BookshelfDeleteScreenUiState(title = nextLoremIpsum()),
            onDismissRequest = {},
            onDismissClick = {},
            onConfirmClick = {}
        )
    }
}
