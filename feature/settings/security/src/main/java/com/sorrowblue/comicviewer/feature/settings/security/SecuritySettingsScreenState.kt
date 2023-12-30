package com.sorrowblue.comicviewer.feature.settings.security

import androidx.activity.result.ActivityResult
import androidx.compose.material3.SnackbarHostState

internal interface SecuritySettingsScreenState {
    fun activityResult(activityResult: ActivityResult)
    fun onChangeBackgroundLockEnabled(value: Boolean)
    fun onChangeBiometricEnabled(value: Boolean)
    fun onResume()
    fun onBiometricsDialogClick()

    fun onBiometricsDialogDismissRequest()
    val snackbarHostState: SnackbarHostState
    var uiState: SecuritySettingsScreenUiState
}
