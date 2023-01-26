package com.sorrowblue.comicviewer.settings.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsSecurityFragmentViewModel @Inject constructor(
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase
) : ViewModel() {

    val securitySettingsFlow = manageSecuritySettingsUseCase.settings

    fun updateUseBiometrics(newValue: Boolean) {
        viewModelScope.launch {
            manageSecuritySettingsUseCase.edit { it.copy(useBiometrics = newValue) }
        }
    }
}
