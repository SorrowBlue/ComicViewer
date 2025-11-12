package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable
context(context: SecuritySettingsScreenContext)
fun SecuritySettingsScreenRoot(
    onBackClick: () -> Unit,
    onChangeAuthEnable: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
) {
    val state = rememberSecuritySettingsScreenState()

    SecuritySettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onChangeAuthEnable = onChangeAuthEnable,
        onPasswordChangeClick = onPasswordChangeClick,
        onChangeBiometricEnable = state::onChangeBiometricEnabled,
        onChangeBackgroundLockEnable = state::onChangeBackgroundLockEnabled,
    )

    if (state.uiState.isBiometricsDialogShow) {
        BiometricsRequestScreen(
            onConfirmClick = state::onBiometricsDialogClick,
            onDismissRequest = state::onBiometricsDialogDismissRequest,
        )
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = state::onResume)
}
