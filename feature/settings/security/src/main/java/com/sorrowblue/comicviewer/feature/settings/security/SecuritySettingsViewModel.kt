package com.sorrowblue.comicviewer.feature.settings.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class SecuritySettingsViewModel(
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
) : ViewModel() {

    val securitySettings = manageSecuritySettingsUseCase.settings

    fun updateLockOnBackground(value: Boolean) {
        viewModelScope.launch {
            manageSecuritySettingsUseCase.edit {
                it.copy(lockOnBackground = value)
            }
        }
    }

    fun updateUseBiometrics(value: Boolean) {
        viewModelScope.launch {
            manageSecuritySettingsUseCase.edit {
                it.copy(useBiometrics = value)
            }
        }
    }
}
