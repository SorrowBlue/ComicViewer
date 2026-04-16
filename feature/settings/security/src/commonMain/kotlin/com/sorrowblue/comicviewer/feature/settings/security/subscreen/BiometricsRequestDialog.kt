package com.sorrowblue.comicviewer.feature.settings.security.subscreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.security.generated.resources.Res
import comicviewer.feature.settings.security.generated.resources.settings_security_biometric_request_label_settings
import comicviewer.feature.settings.security.generated.resources.settings_security_biometric_request_text
import comicviewer.feature.settings.security.generated.resources.settings_security_biometric_request_title
import comicviewer.framework.ui.generated.resources.Res as UiRes
import comicviewer.framework.ui.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BiometricsRequestDialog(onConfirmClick: () -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(Res.string.settings_security_biometric_request_title),
            )
        },
        text = { Text(text = stringResource(Res.string.settings_security_biometric_request_text)) },
        confirmButton = {
            FilledTonalButton(onClick = onConfirmClick) {
                Text(text = stringResource(Res.string.settings_security_biometric_request_label_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(UiRes.string.cancel))
            }
        },
    )
}

@Preview
@Composable
internal fun BiometricsRequestDialogPreview() {
    PreviewTheme {
        var isShow by remember { mutableStateOf(true) }
        if (isShow) {
            BiometricsRequestDialog(
                onConfirmClick = { isShow = false },
                onDismissRequest = { isShow = false },
            )
        }
    }
}
