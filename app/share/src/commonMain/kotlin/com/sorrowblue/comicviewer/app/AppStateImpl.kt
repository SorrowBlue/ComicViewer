/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.SnackbarEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

internal val ProvidesAppState
    @Composable
    get() = LocalAppState provides rememberAppState()

@Composable
private fun rememberAppState(): AppState = remember {
    AppStateImpl()
}

private class AppStateImpl : AppState {

    override val snackbarEvents: SharedFlow<SnackbarEvent>
        field = MutableSharedFlow<SnackbarEvent>(extraBufferCapacity = 16)

    override fun showSnackbar(
        message: String,
        actionLabel: String?,
        duration: SnackbarDuration,
        withDismissAction: Boolean,
        onActionPerformed: (() -> Unit)?,
    ) {
        snackbarEvents.tryEmit(
            SnackbarEvent(
                message = message,
                actionLabel = actionLabel,
                duration = duration,
                withDismissAction = withDismissAction,
                onActionPerformed = onActionPerformed,
            ),
        )
    }
}
