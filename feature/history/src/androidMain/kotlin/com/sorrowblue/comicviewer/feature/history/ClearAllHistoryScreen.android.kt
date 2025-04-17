package com.sorrowblue.comicviewer.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@PreviewLightDark
@Composable
private fun ClearAllHistoryScreenPreview() {
    PreviewTheme {
        ClearAllHistoryScreen(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}
