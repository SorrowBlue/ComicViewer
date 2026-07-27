/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AppState {
    return remember(snackbarHostState) {
        AppStateImpl(
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
private class AppStateImpl(
    override val snackbarHostState: SnackbarHostState,
    override val coroutineScope: CoroutineScope,
) : AppState

internal val ProvidesAppState
    @Composable
    get() = LocalAppState provides rememberAppState()
