/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): NotificationPermissionRequester = remember {
    object : NotificationPermissionRequester {
        override fun requestPermission(action: () -> Unit, showInContextUI: () -> Unit) {
            action()
        }

        override fun checkNotificationPermission(): Boolean = true

        override fun openNotificationSettings() {
            // No-op
        }
    }
}
