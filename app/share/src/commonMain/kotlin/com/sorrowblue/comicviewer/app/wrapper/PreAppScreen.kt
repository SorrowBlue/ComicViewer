/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app.wrapper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenRoot
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenRoot
import logcat.logcat

private const val TAG = "RootScreenWrapper"

@Composable
internal fun PreAppScreen(
    isInitialized: Boolean,
    onInitialize: () -> Unit,
    finishApp: () -> Unit,
    content: @Composable () -> Unit,
) {
    val state = rememberPreAppScreenState()
    val currentOnInitialized by rememberUpdatedState(onInitialize)
    LaunchedEffect(state.uiState) {
        logcat(TAG) { "PreAppScreenState: ${state.uiState}" }
    }
    if (state.uiState == PreAppUiState.TutorialRequired) {
        TutorialScreenRoot(onComplete = state::onTutorialComplete)
        SideEffect {
            currentOnInitialized()
        }
    } else {
        if (isInitialized || state.uiState == PreAppUiState.NoAuthRequired ||
            (state.uiState as? PreAppUiState.AuthRequired)?.authed == true
        ) {
            content()
        }
        if (state.uiState is PreAppUiState.AuthRequired) {
            AnimatedVisibility(
                visible = (!(state.uiState as PreAppUiState.AuthRequired).authed) || !isInitialized,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                AuthenticationScreenRoot(
                    screenType = ScreenType.Authenticate,
                    onBackClick = finishApp,
                    onComplete = state::onAuthComplete,
                )
                SideEffect {
                    currentOnInitialized()
                }
            }
        }
        if (state.uiState == PreAppUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
