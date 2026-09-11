/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.runtime.Composable

internal interface NotificationPermissionRequester {

    fun requestPermission(action: () -> Unit, showInContextUI: () -> Unit)

    fun checkNotificationPermission(): Boolean

    fun openNotificationSettings()
}

@Composable
internal expect fun rememberNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): NotificationPermissionRequester
