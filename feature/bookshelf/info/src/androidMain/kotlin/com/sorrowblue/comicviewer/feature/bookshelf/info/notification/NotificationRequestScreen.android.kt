package com.sorrowblue.comicviewer.feature.bookshelf.info.notification

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@PreviewMultiScreen
@Composable
private fun NotificationRequestScreenPreview() {
    PreviewTheme {
        NotificationRequestScreen(
            type = ScanType.File,
            onDismissRequest = {},
            onConfirmClick = {},
            onNotAllowedClick = {},
            onCancelClick = {}
        )
    }
}
