@file:OptIn(ExperimentalMaterial3Api::class)

package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.R

sealed interface BookshelfRemoveDialogUiState {

    data object Hide : BookshelfRemoveDialogUiState

    data class Show(val title: String) : BookshelfRemoveDialogUiState
}

@Composable
fun BookshelfRemoveDialog(
    uiState: BookshelfRemoveDialogUiState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    if (uiState is BookshelfRemoveDialogUiState.Show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.bookshelf_remove_title))
            },
            text = {
                Text(text = stringResource(id = R.string.bookshelf_remove_label, uiState.title))
            },
            confirmButton = {
                TextButton(onClick = onConfirmClick) {
                    Text(stringResource(id = R.string.bookshelf_remove_label_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        )
    }
}
