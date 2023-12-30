package com.sorrowblue.comicviewer.feature.favorite.common.component

import android.os.Parcelable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.favorite.common.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoriteCreateDialogUiState(
    val name: String = "",
    val nameError: Boolean = false,
    val isShown: Boolean = false,
) : Parcelable

@Composable
fun FavoriteNameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = {
            Text(text = stringResource(id = R.string.favorite_common_label_favorite_name))
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = stringResource(id = R.string.favorite_common_message_error))
            }
        },
        modifier = modifier
    )
}

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
                FavoriteNameTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    isError = uiState.nameError,
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
