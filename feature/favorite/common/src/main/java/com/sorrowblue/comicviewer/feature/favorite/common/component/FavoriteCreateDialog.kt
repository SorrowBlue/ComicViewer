package com.sorrowblue.comicviewer.feature.favorite.common.component

import android.os.Parcelable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.common.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoriteCreateDialogUiState(
    val name: String = "",
    val nameError: Boolean = false,
    val isShown: Boolean = false,
) : Parcelable

@Composable
fun FavoriteCreateDialog(
    uiState: FavoriteCreateDialogUiState,
    onNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onCreateClick: () -> Unit,
) {
    if (uiState.isShown) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.favorite_common_title_create)) },
            text = {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = {
                        Text(text = stringResource(id = R.string.favorite_common_label_favorite_name))
                    },
                    isError = uiState.nameError,
                    supportingText = {
                        if (uiState.nameError) {
                            Text(text = stringResource(id = R.string.favorite_common_message_error))
                        }
                    }
                )
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onCreateClick) {
                    Text(text = stringResource(id = R.string.favorite_common_label_create))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            }
        )
    }
}
