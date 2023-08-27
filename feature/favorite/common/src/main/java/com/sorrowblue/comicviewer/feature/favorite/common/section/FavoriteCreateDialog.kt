package com.sorrowblue.comicviewer.feature.favorite.common.section

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

sealed interface FavoriteCreateDialogUiState {
    data class Show(
        val name: String,
    ) : FavoriteCreateDialogUiState

    data object Hide : FavoriteCreateDialogUiState
}

@Composable
fun FavoriteCreateDialog(
    uiState: FavoriteCreateDialogUiState,
    onNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onCreateClick: () -> Unit
) {
    if (uiState is FavoriteCreateDialogUiState.Show) {
        AlertDialog(
            title = { Text("新しいお気に入り") },
            text = { OutlinedTextField(value = uiState.name, onValueChange = onNameChange) },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onCreateClick) {
                    Text("作成")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("キャンセル")
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewFavoriteCreateDialog() {
    FavoriteCreateDialog(FavoriteCreateDialogUiState.Hide, {}, {}, {})
}
