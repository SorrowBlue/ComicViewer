package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.koinInject

@Composable
internal actual fun rememberSecuritySettingsScreenState(
    scope: CoroutineScope,
    manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    ): SecuritySettingsScreenState {
    return remember { SecuritySettingsScreenStateImpl() }
}

private class SecuritySettingsScreenStateImpl : SecuritySettingsScreenState {

    override fun onChangeBackgroundLockEnabled(value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onChangeBiometricEnabled(value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        TODO("Not yet implemented")
    }

    override fun onBiometricsDialogClick() {
        TODO("Not yet implemented")
    }

    override fun onBiometricsDialogDismissRequest() {
        TODO("Not yet implemented")
    }

    override val snackbarHostState: SnackbarHostState
        get() = TODO("Not yet implemented")
    override var uiState: SecuritySettingsScreenUiState
        get() = TODO("Not yet implemented")
        set(value) {}
}
