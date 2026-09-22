/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionState
import com.sorrowblue.comicviewer.framework.permission.localnetwork.rememberLocalNetworkPermissionRequester
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

@Composable
internal fun LocalNetworkAccessPermissionScreenRoot(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val requester = rememberLocalNetworkPermissionRequester(initCheck = true)
    val state = requester.state
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    LaunchedEffect(requester) {
        snapshotFlow { requester.state }
            .filterIsInstance<LocalNetworkPermissionState.Granted>()
            .first()
        currentOnDismissRequest()
    }

    if (state is LocalNetworkPermissionState.Rationale ||
        state is LocalNetworkPermissionState.DeniedPermanent
    ) {
        LocalNetworkAccessPermissionScreen(
            isRationale = state is LocalNetworkPermissionState.Rationale,
            onConfirmClick = requester::onPermissionConfirmClick,
            onDismissClick = onDismissRequest,
            modifier = modifier,
        )
    }
}
