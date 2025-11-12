package com.sorrowblue.comicviewer.feature.bookshelf.info.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberNotificationRequestScreenState(): NotificationRequestScreenState =
    remember {
        object : NotificationRequestScreenState {
            override fun onConfirmClick(onComplete: () -> Unit) {
                // Do nothing on non-Android platforms
                onComplete()
            }
        }
    }
