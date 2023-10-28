package com.sorrowblue.comicviewer.feature.settings.security.section

import androidx.compose.runtime.Composable
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
        title = "Device settings required",
        text = "Please set ScreenLock or BiometricAuthentication in device settings.",
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
