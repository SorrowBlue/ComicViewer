package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable
context(context: SecuritySettingsScreenContext)
internal fun SecuritySettingsScreenRoot(
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
        modifier = Modifier.testTag("SecuritySettingsRoot"),
    )

    if (state.uiState.isBiometricsDialogShow) {
        BiometricsRequestDialog(
            onConfirmClick = state::onBiometricsDialogClick,
            onDismissRequest = state::onBiometricsDialogDismissRequest,
        )
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = state::onResume)
}
