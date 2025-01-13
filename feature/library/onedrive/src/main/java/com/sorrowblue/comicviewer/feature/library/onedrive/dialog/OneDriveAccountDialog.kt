package com.sorrowblue.comicviewer.feature.library.onedrive.dialog

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.feature.library.onedrive.R
import com.sorrowblue.comicviewer.feature.library.onedrive.navigation.OneDriveGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import java.io.InputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Destination<OneDriveGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun OneDriveAccountDialog(
    destinationsNavigator: DestinationsNavigator,
    state: OneDriveAccountDialogState = rememberOneDriveAccountDialogState(),
) {
    OneDriveAccountDialog(
        uiState = state.uiState,
        onDismissRequest = destinationsNavigator::popBackStack,
        onSignOutClick = { state.onSignOutClick { destinationsNavigator.popBackStack() } }
    )
}

internal data class OneDriveDialogUiState(
    val name: String = "",
    val photoUrl: (suspend () -> InputStream)? = null,
)

@Composable
private fun OneDriveAccountDialog(
    uiState: OneDriveDialogUiState,
    onDismissRequest: () -> Unit,
    onSignOutClick: () -> Unit,
    dispatchers: CoroutineDispatcher = Dispatchers.IO,
) {
    var inputStream by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(uiState.photoUrl) {
        launch(dispatchers) {
            inputStream = uiState.photoUrl?.invoke()?.readBytes()
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onSignOutClick) {
                Icon(imageVector = ComicIcons.Logout, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(R.string.onedrive_action_signout))
            }
        },
        icon = {
            AsyncImage(
                model = inputStream,
                contentDescription = null,
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
            OneDriveAccountDialog(
                uiState = OneDriveDialogUiState(),
                onDismissRequest = {},
                onSignOutClick = {}
            )
        }
    }
}
