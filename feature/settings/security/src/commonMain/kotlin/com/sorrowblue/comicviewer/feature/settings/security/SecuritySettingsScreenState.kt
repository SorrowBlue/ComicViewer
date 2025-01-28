package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

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
internal expect fun rememberSecuritySettingsScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase = koinInject(),
): SecuritySettingsScreenState

