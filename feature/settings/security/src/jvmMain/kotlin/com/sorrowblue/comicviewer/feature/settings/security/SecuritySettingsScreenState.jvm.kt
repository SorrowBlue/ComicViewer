/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal actual fun rememberSecuritySettingsScreenState(
    viewModel: SecuritySettingsViewModel,
): SecuritySettingsScreenState {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember(scope, snackbarHostState, lifecycle, viewModel) {
        SecuritySettingsScreenStateImpl(
            scope = scope,
            lifecycle = lifecycle,
            snackbarHostState = snackbarHostState,
            settingsFlow = viewModel.settingsFlow,
            updateSettings = viewModel::updateSettings,
        )
    }
}

private class SecuritySettingsScreenStateImpl(
    scope: CoroutineScope,
    lifecycle: Lifecycle,
    override val snackbarHostState: SnackbarHostState,
    settingsFlow: StateFlow<SecuritySettings>,
    private val updateSettings: ((SecuritySettings) -> SecuritySettings) -> Unit,
) : SecuritySettingsScreenState {
    override var uiState by mutableStateOf(SecuritySettingsScreenUiState())

    init {
        uiState = uiState.copy(isBiometricCanBeUsed = false)
        settingsFlow.flowWithLifecycle(lifecycle)
            .onEach {
                uiState = uiState.copy(
                    isAuthEnabled = it.password != null,
                    isBackgroundLockEnabled = it.lockOnBackground,
                    isBiometricEnabled = it.useBiometrics,
                )
            }.launchIn(scope)
    }

    override fun onChangeBackgroundLockEnabled(value: Boolean) {
        updateSettings {
            it.copy(lockOnBackground = value)
        }
    }

    override fun onChangeBiometricEnabled(value: Boolean) {
        // Do nothing.
    }

    override fun onResume() {
        // Do nothing.
    }

    override fun onBiometricsDialogClick() {
        // Do nothing.
    }

    override fun onBiometricsDialogDismissRequest() {
        // Do nothing.
    }
}
