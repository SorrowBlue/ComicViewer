package com.sorrowblue.comicviewer.framework.permission.localnetwork

import androidx.compose.runtime.Composable

sealed interface LocalNetworkPermissionState {

    data object Pending : LocalNetworkPermissionState

    data object Granted : LocalNetworkPermissionState

    data object Rationale : LocalNetworkPermissionState

    data object DeniedPermanent : LocalNetworkPermissionState
}

interface LocalNetworkPermissionRequester {

    val state: LocalNetworkPermissionState get() = LocalNetworkPermissionState.Granted

    fun checkPermission(): Boolean = true

    fun onPermissionConfirmClick() {
        // No-op
    }

    fun reset() {
        // No-op
    }
}

@Composable
expect fun rememberLocalNetworkPermissionRequester(
    initCheck: Boolean = true,
): LocalNetworkPermissionRequester
