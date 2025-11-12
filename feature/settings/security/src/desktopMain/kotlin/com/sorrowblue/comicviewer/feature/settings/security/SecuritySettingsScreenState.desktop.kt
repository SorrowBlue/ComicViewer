package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
context(context: SecuritySettingsScreenContext)
internal actual fun rememberSecuritySettingsScreenState(): SecuritySettingsScreenState {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val manageSecuritySettingsUseCase = context.manageSecuritySettingsUseCase
    return remember {
        SecuritySettingsScreenStateImpl(
            scope = scope,
            manageSecuritySettingsUseCase = manageSecuritySettingsUseCase,
            snackbarHostState = snackbarHostState,
        )
    }
}

private class SecuritySettingsScreenStateImpl(
    private val scope: CoroutineScope,
    override val snackbarHostState: SnackbarHostState,
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
) : SecuritySettingsScreenState {
    override var uiState by mutableStateOf(SecuritySettingsScreenUiState())

    init {
        uiState = uiState.copy(isBiometricCanBeUsed = false)
        manageSecuritySettingsUseCase.settings
            .onEach {
                uiState = uiState.copy(
                    isAuthEnabled = it.password != null,
                    isBackgroundLockEnabled = it.lockOnBackground,
                    isBiometricEnabled = it.useBiometrics,
                )
            }.launchIn(scope)
    }

    override fun onChangeBackgroundLockEnabled(value: Boolean) {
        scope.launch {
            manageSecuritySettingsUseCase.edit {
                it.copy(lockOnBackground = value)
            }
        }
    }

    override fun onChangeBiometricEnabled(value: Boolean) {
        // TODO()
    }

    override fun onResume() {
        // TODO()
    }

    override fun onBiometricsDialogClick() {
        // TODO()
    }

    override fun onBiometricsDialogDismissRequest() {
        // TODO()
    }
}
