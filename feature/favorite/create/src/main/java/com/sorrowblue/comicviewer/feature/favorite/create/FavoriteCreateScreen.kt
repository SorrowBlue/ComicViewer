package com.sorrowblue.comicviewer.feature.favorite.create

import android.view.View
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.framework.compose.CollectAsEffect

internal data class FavoriteCreateScreenUiState(
    val name: String = "",
    val error: Int = View.NO_ID,
)

internal sealed interface FavoriteCreateScreenUiEvent {
    data object DoneCreate : FavoriteCreateScreenUiEvent
}

@Composable
internal fun FavoriteCreateRoute(
    onDismissRequest: () -> Unit,
    viewModel: FavoriteCreateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    FavoriteCreateScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onDismissRequest = onDismissRequest,
        onCreateClick = viewModel::onCreateClick,
    )
    viewModel.uiEvent.CollectAsEffect {
        when (it) {
            FavoriteCreateScreenUiEvent.DoneCreate -> onDismissRequest()
        }
    }
}

@Composable
private fun FavoriteCreateScreen(
    uiState: FavoriteCreateScreenUiState = FavoriteCreateScreenUiState(),
    onNameChange: (String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onCreateClick: () -> Unit = {},
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.favorite_create_title)) },
        text = {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = {
                    Text(text = stringResource(id = R.string.favorite_create_label_favorite_name))
                },
                isError = uiState.error != View.NO_ID,
                supportingText = {
                    if (uiState.error != View.NO_ID) {
                        Text(text = stringResource(id = uiState.error))
                    }
                }
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onCreateClick) {
                Text(text = stringResource(id = R.string.favorite_create_label_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    )
}

@Preview
@Composable
private fun PreviewFavoriteCreateScreen() {
    FavoriteCreateScreen()
}
