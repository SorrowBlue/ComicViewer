package com.sorrowblue.comicviewer.feature.library.onedrive.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.library.onedrive.OneDriveSignInDialogState
import com.sorrowblue.comicviewer.feature.library.onedrive.R
import com.sorrowblue.comicviewer.feature.library.onedrive.rememberOneDriveSignInDialogState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun OneDriveSignInDialog(
    onDismissRequest: () -> Unit,
    state: OneDriveSignInDialogState = rememberOneDriveSignInDialogState(),
) {
    OneDriveSignInDialog(onDismissRequest = onDismissRequest, onSignInClick = state::onSignInClick)
}

@Composable
private fun OneDriveSignInDialog(
    onDismissRequest: () -> Unit,
    onSignInClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onSignInClick) {
                Icon(imageVector = ComicIcons.Logout, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(R.string.onedrive_action_signin))
            }
        },
        icon = {
            Icon(ComicIcons.AccountCircle, null, modifier = Modifier.size(40.dp))
        },
    )
}
