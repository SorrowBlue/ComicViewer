/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.SharedFlow

val LocalAppState = staticCompositionLocalOf<AppState> {
    error("No AppState provided")
}

@OptIn(ExperimentalSharedTransitionApi::class)
interface AppState {
    val snackbarEvents: SharedFlow<SnackbarEvent>
    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        withDismissAction: Boolean = false,
        onActionPerformed: (() -> Unit)? = null,
    )
}

data class SnackbarEvent(
    val message: String,
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val withDismissAction: Boolean = false,
    val onActionPerformed: (() -> Unit)? = null,
)
