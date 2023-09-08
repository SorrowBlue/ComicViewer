package com.sorrowblue.comicviewer.feature.settings.security.section

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

internal sealed interface BiometricsRequestDialogUiState {

    data object Hide : BiometricsRequestDialogUiState

    data object Show : BiometricsRequestDialogUiState
}

@Composable
internal fun BiometricsDialog(
    uiState: BiometricsRequestDialogUiState = BiometricsRequestDialogUiState.Show,
    onNextClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    if (uiState == BiometricsRequestDialogUiState.Hide) return
    AlertDialog(
        title = {
            Text(text = "Device settings required")
        },
        text = {
            Text(text = "Please set ScreenLock or BiometricAuthentication in device settings.")
        },
        confirmButton = {
            FilledTonalButton(onClick = onNextClick) {
                Text(text = "Next")
            }
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    )
}

@Preview
@Composable
internal fun PreviewBiometricsRequestDialog() {
    AppMaterialTheme {
        BiometricsDialog()
    }
}
