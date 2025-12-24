package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

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
