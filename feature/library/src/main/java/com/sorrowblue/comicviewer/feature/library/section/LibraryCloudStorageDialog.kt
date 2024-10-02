package com.sorrowblue.comicviewer.feature.library.section

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.feature.library.R
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
internal fun LibraryCloudStorageDialog(
    addOn: Feature.AddOn,
    onInstallClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { },
        icon = {
            Icon(imageVector = ComicIcons.RocketLaunch, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.library_title_extensions))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.library_need_install_extension,
                    stringResource(id = addOn.label)
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onInstallClick) {
                Text(text = stringResource(R.string.library_action_Install))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelClick) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

@Preview
@Composable
private fun PreviewLibraryCloudStorageDialog() {
    PreviewTheme {
        LibraryCloudStorageDialog(
            addOn = Feature.AddOn.GoogleDrive(AddOnItemState.Still),
            onInstallClick = {},
            onCancelClick = {}
        )
    }
}
