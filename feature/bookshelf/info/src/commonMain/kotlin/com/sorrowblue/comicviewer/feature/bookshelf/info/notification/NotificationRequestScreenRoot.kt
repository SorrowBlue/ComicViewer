package com.sorrowblue.comicviewer.feature.bookshelf.info.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
internal fun NotificationRequestScreenRoot(scanType: ScanType, onBackClick: () -> Unit) {
    val state = rememberNotificationRequestScreenState()
    NotificationRequestScreen(
        scanType = scanType,
        onDismissRequest = onBackClick,
        onConfirmClick = { state.onConfirmClick(onBackClick) },
        modifier = Modifier.testTag("BookshelfNotificationScreenRoot"),
    )
}
