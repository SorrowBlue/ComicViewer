package com.sorrowblue.comicviewer.feature.library.googledrive.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.feature.library.googledrive.R
import com.sorrowblue.comicviewer.feature.library.googledrive.navigation.GoogleDriveGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.nextLoremIpsum

internal data class GoogleSignOutDialogUiState(
    val photoUrl: String = "",
    val name: String = "",
)

@Destination<GoogleDriveGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun GoogleSignOutDialog(
    destinationsNavigator: DestinationsNavigator,
    state: GoogleSignOutDialogState = rememberGoogleSignOutDialogState(),
) {
    GoogleSignOutDialog(
        uiState = state.uiState,
        onDismissRequest = destinationsNavigator::popBackStack,
        onSignOutClick = {
            state.onSignOutClick {
                destinationsNavigator.popBackStack()
            }
        }
    )
}

@Composable
private fun GoogleSignOutDialog(
    uiState: GoogleSignOutDialogUiState,
    onDismissRequest: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onSignOutClick) {
                Icon(imageVector = ComicIcons.Logout, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(R.string.googledrive_action_logout))
            }
        },
        icon = {
            AsyncImage(
                model = uiState.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AlertDialogDefaults.iconContentColor)
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
private fun PreviewGoogleSignOutDialog() {
    ComicTheme {
        Surface {
            GoogleSignOutDialog(
                uiState = GoogleSignOutDialogUiState("aaaaaa", nextLoremIpsum()),
                onDismissRequest = {},
                onSignOutClick = {}
            )
        }
    }
}
