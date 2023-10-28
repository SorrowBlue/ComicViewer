package com.sorrowblue.comicviewer.feature.settings.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsSecurityViewModel @Inject constructor(
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
