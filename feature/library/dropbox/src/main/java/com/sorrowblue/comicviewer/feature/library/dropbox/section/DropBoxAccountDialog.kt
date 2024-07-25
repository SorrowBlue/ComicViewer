package com.sorrowblue.comicviewer.feature.library.dropbox.section

import android.os.Parcelable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sorrowblue.comicviewer.feature.library.dropbox.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.previewPlaceholder
import kotlinx.parcelize.Parcelize

internal sealed interface DropBoxDialogUiState : Parcelable {

    @Parcelize
    data object Hide : DropBoxDialogUiState

    @Parcelize
    data class Show(
        val photoUrl: String = "",
        val name: String = "",
    ) : DropBoxDialogUiState
}

@Composable
internal fun DropBoxAccountDialog(
    uiState: DropBoxDialogUiState,
    onDismissRequest: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    when (uiState) {
        DropBoxDialogUiState.Hide -> Unit
        is DropBoxDialogUiState.Show ->
            DropBoxAccountDialog(
                uiState = uiState,
                onDismissRequest = onDismissRequest,
                onLogoutClick = onLogoutClick,
            )
    }
}

@Composable
private fun DropBoxAccountDialog(
    uiState: DropBoxDialogUiState.Show = DropBoxDialogUiState.Show(),
    onDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onLogoutClick) {
                Icon(imageVector = ComicIcons.Logout, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(R.string.dropbox_action_logout))
            }
        },
        icon = {
            AsyncImage(
                model = uiState.photoUrl,
                contentDescription = null,
                placeholder = previewPlaceholder(),
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
private fun PreviewGoogleAccountDialog() {
    ComicTheme {
        Surface {
            DropBoxAccountDialog()
        }
    }
}
