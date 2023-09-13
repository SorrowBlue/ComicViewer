package com.sorrowblue.comicviewer.feature.library.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

internal sealed interface RequestInstallDialogUiState {

    data object Hide : RequestInstallDialogUiState
    data class Show(val feature: Feature.AddOn) : RequestInstallDialogUiState
}

@Composable
internal fun LibraryCloudStorageDialog(
    uiState: RequestInstallDialogUiState,
    onInstallClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    if (uiState is RequestInstallDialogUiState.Show) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(imageVector = Icons.TwoTone.RocketLaunch, contentDescription = null)
            },
            title = {
                Text(text = "拡張機能")
            },
            text = {
                Text(text = stringResource(id = uiState.feature.label) + "を利用するには、拡張機能をインストール必要があります。")
            },
            confirmButton = {
                TextButton(onClick = onInstallClick) {
                    Text(text = "インストール")
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
}

@Preview
@Composable
fun PreviewLibraryCloudStorageDialog() {
    AppMaterialTheme {
        LibraryCloudStorageDialog(
            RequestInstallDialogUiState.Show(Feature.AddOn.GoogleDrive(AddOnItemState.Still)),
            {},
            {}
        )
    }
}
