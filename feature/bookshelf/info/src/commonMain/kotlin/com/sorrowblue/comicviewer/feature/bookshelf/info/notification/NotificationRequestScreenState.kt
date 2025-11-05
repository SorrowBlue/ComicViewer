package com.sorrowblue.comicviewer.feature.bookshelf.info.notification

import androidx.compose.runtime.Composable

internal sealed interface NotificationRequestScreenEvent {
    object Complete : NotificationRequestScreenEvent
}

internal interface NotificationRequestScreenState {
    fun onConfirmClick(onComplete: () -> Unit)
}

@Composable
internal expect fun rememberNotificationRequestScreenState(): NotificationRequestScreenState
