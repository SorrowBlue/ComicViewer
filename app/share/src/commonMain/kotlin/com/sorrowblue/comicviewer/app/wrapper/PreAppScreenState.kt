/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app.wrapper

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

sealed interface PreAppUiState {
    data object Loading : PreAppUiState
    data object TutorialRequired : PreAppUiState
    data class AuthRequired(val authed: Boolean) : PreAppUiState
    data object NoAuthRequired : PreAppUiState
}

@Composable
internal fun rememberPreAppScreenState(
    viewModel: PreAppViewModel = metroViewModel<PreAppViewModel>(),
): PreAppScreenState {
    val coroutineScope = rememberCoroutineScope()
    val state = remember(coroutineScope, viewModel) {
        PreAppScreenStateImpl(
            scope = coroutineScope,
            tutorialRequired = viewModel.tutorialRequired,
            authRequired = viewModel.authRequired,
            lockOnBackground = viewModel.lockOnBackground,
            tutorialComplete = viewModel::completeTutorial,
        )
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE, onEvent = state::onPause)
    return state
}

internal interface PreAppScreenState {
    val uiState: PreAppUiState

    fun onAuthComplete()
    fun onTutorialComplete()
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal class PreAppScreenStateImpl(
    scope: CoroutineScope,
    tutorialRequired: SharedFlow<Boolean>,
    authRequired: SharedFlow<Boolean>,
    private val lockOnBackground: StateFlow<Boolean>,
    private val tutorialComplete: () -> Unit = {},
) : PreAppScreenState {

    override var uiState: PreAppUiState by mutableStateOf(PreAppUiState.Loading)
        private set

    init {
        combine(tutorialRequired, authRequired) { tutorialRequired1, authRequired1 ->
            uiState = when {
                tutorialRequired1 -> PreAppUiState.TutorialRequired

                authRequired1 -> {
                    when (val uiState = uiState) {
                        is PreAppUiState.AuthRequired -> PreAppUiState.AuthRequired(uiState.authed)
                        PreAppUiState.Loading -> PreAppUiState.AuthRequired(false)
                        PreAppUiState.NoAuthRequired -> PreAppUiState.AuthRequired(true)
                        PreAppUiState.TutorialRequired -> PreAppUiState.AuthRequired(false)
                    }
                }

                else -> PreAppUiState.NoAuthRequired
            }
        }.launchIn(scope)
    }

    override fun onAuthComplete() {
        uiState = PreAppUiState.AuthRequired(true)
    }

    override fun onTutorialComplete() {
        tutorialComplete()
    }

    fun onPause() {
        if (uiState is PreAppUiState.AuthRequired && lockOnBackground.value) {
            uiState = PreAppUiState.AuthRequired(authed = false)
        }
    }
}
