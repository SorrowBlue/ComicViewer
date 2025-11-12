package com.sorrowblue.comicviewer.feature.bookshelf.info.notification

import androidx.compose.runtime.Composable

@Composable
fun NotificationRequestScreenRoot(scanType: ScanType, onBackClick: () -> Unit) {
    val state = rememberNotificationRequestScreenState()
    NotificationRequestScreen(
        scanType = scanType,
        onDismissRequest = onBackClick,
        onConfirmClick = { state.onConfirmClick(onBackClick) },
    )
}
