/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState

@Composable
internal fun AppSnackbarEffect(
    snackbarHostState: SnackbarHostState,
    appState: AppState = LocalAppState.current,
) {
    val lifecycle = LocalLifecycleOwner.current
    LaunchedEffect(appState) {
        appState.snackbarEvents.flowWithLifecycle(lifecycle.lifecycle).collect { event ->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = event.duration,
                withDismissAction = event.withDismissAction,
            )
            if (result == SnackbarResult.ActionPerformed) {
                event.onActionPerformed?.invoke()
            }
        }
    }
}
