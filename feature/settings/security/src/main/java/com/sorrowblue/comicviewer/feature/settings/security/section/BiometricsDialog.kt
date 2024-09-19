package com.sorrowblue.comicviewer.feature.settings.security.section

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.feature.settings.security.R
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
internal fun BiometricsDialog(
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = stringResource(R.string.settings_security_title_device_settings_required),
        text = stringResource(R.string.settings_security_text_dialog_desc),
        confirmButton = {
            FilledTonalButton(
                onClick = onConfirmClick,
            ) {
                Text(text = stringResource(id = R.string.settings_security_label_to_settings))
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
    PreviewTheme {
        var isShow by remember { mutableStateOf(true) }
        if (isShow) {
            BiometricsDialog(
                onConfirmClick = { isShow = false },
                onDismissRequest = { isShow = false }
            )
        }
    }
}
