package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

internal interface SecuritySettingsScreenState {
    fun onChangeBackgroundLockEnabled(value: Boolean)
    fun onChangeBiometricEnabled(value: Boolean)
    fun onResume()
    fun onBiometricsDialogClick()

    fun onBiometricsDialogDismissRequest()
    val snackbarHostState: SnackbarHostState
    var uiState: SecuritySettingsScreenUiState
}

@Composable
context(context: SecuritySettingsScreenContext)
internal expect fun rememberSecuritySettingsScreenState(): SecuritySettingsScreenState
