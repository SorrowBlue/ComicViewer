package com.sorrowblue.comicviewer.folder.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun FolderScanInfoDialog(
    isShown: Boolean = true,
    onConfirmClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    if (isShown) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = { Icon(ComicIcons.PermDeviceInformation, contentDescription = null) },
            title = {
                Text(text = "Request Notification Permission")
            },
            text = {
                Column {
                    Text(stringResource(id = R.string.folder_message_scan))
                    Spacer(modifier = Modifier.size(ComicTheme.dimension.spacer))
                    Row {
                        Icon(ComicIcons.Info, contentDescription = null)
                        Spacer(modifier = Modifier.size(ComicTheme.dimension.spacer))
                        Text(stringResource(id = R.string.folder_message_info_scan))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmClick) {
                    Text(text = "Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "No")
                }
            })
    }
}

@Preview(locale = "ja")
@Composable
fun PreviewFolderScanInfoDialog() {
    ComicTheme {
        FolderScanInfoDialog()
    }
}
