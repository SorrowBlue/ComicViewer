package com.sorrowblue.comicviewer.feature.library.googledrive.section

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
import com.sorrowblue.comicviewer.feature.library.googledrive.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.previewPlaceholder
import kotlinx.parcelize.Parcelize

internal sealed interface GoogleAccountDialogUiState : Parcelable {

    @Parcelize
    data object Hide : GoogleAccountDialogUiState

    @Parcelize
    data class Show(
        val photoUrl: String = "",
        val name: String = "",
    ) : GoogleAccountDialogUiState
}

@Composable
internal fun GoogleAccountDialog(
    uiState: GoogleAccountDialogUiState,
    onDismissRequest: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    when (uiState) {
        GoogleAccountDialogUiState.Hide -> Unit
        is GoogleAccountDialogUiState.Show ->
            GoogleAccountDialog(
                uiState = uiState,
                onDismissRequest = onDismissRequest,
                onLogoutClick = onLogoutClick,
            )
    }
}

@Composable
private fun GoogleAccountDialog(
    uiState: GoogleAccountDialogUiState.Show = GoogleAccountDialogUiState.Show(),
    onDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onLogoutClick) {
                Icon(imageVector = ComicIcons.Logout, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(R.string.googledrive_action_logout))
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
            GoogleAccountDialog()
        }
    }
}
