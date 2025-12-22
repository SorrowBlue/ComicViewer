package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import comicviewer.feature.settings.security.generated.resources.Res
import comicviewer.feature.settings.security.generated.resources.settings_security_label_to_settings
import comicviewer.feature.settings.security.generated.resources.settings_security_text_dialog_desc
import comicviewer.feature.settings.security.generated.resources.settings_security_title_device_settings_required
import comicviewer.framework.ui.generated.resources.Res as UiRes
import comicviewer.framework.ui.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BiometricsRequestDialog(onConfirmClick: () -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(Res.string.settings_security_title_device_settings_required),
            )
        },
        text = { Text(text = stringResource(Res.string.settings_security_text_dialog_desc)) },
        confirmButton = {
            FilledTonalButton(onClick = onConfirmClick) {
                Text(text = stringResource(Res.string.settings_security_label_to_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(UiRes.string.cancel))
            }
        },
    )
}
