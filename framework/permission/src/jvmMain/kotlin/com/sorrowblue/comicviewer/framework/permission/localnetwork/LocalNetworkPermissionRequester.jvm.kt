/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.permission.localnetwork

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberLocalNetworkPermissionRequester(initCheck: Boolean): LocalNetworkPermissionRequester {
    return remember {
        JvmLocalNetworkPermissionRequester()
    }
}

private class JvmLocalNetworkPermissionRequester : LocalNetworkPermissionRequester
