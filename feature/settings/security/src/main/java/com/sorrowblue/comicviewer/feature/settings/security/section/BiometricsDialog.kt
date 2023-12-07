package com.sorrowblue.comicviewer.feature.settings.security.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.feature.settings.security.R
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.FilledTonalButton
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.TextButton

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
                text = R.string.settings_security_label_to_settings
            )
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                text = android.R.string.cancel
            )
        }
    )
}

@Preview
@Composable
internal fun PreviewBiometricsRequestDialog() {
    PreviewTheme {
        BiometricsDialog(onConfirmClick = {}, onDismissRequest = {})
    }
}
