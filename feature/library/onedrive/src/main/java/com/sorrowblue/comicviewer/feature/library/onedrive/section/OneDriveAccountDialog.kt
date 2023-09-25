package com.sorrowblue.comicviewer.feature.library.onedrive.section

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.debugPlaceholder

internal sealed interface OneDriveDialogUiState {

    data object Hide : OneDriveDialogUiState
    data class Show(
        val photoUrl: String = "",
        val name: String = ""
    ) : OneDriveDialogUiState
}

@Composable
internal fun OneDriveAccountDialog(
    uiState: OneDriveDialogUiState,
    onDismissRequest: () -> Unit,
    onLogoutClick: () -> Unit
) {
    when (uiState) {
        OneDriveDialogUiState.Hide -> Unit
        is OneDriveDialogUiState.Show ->
            OneDriveAccountDialog(
                uiState = uiState,
                onDismissRequest = onDismissRequest,
                onLogoutClick = onLogoutClick,
            )
    }
}

@Composable
private fun OneDriveAccountDialog(
    uiState: OneDriveDialogUiState.Show = OneDriveDialogUiState.Show(),
    onDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onLogoutClick) {
                Icon(imageVector = ComicIcons.Logout, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "Logout")
            }
        },
        icon = {
            AsyncImage(
                model = uiState.photoUrl,
                contentDescription = null,
                placeholder = debugPlaceholder(),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        },
        title = {
            Text(
                text = uiState.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    )
}

@Preview
@Composable
private fun PreviewOneDriveAccountDialog() {
    ComicTheme {
        Surface {
            OneDriveAccountDialog()
        }
    }
}
