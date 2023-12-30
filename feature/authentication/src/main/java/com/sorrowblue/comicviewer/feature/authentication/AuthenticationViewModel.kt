package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Stable
@HiltViewModel
internal class AuthenticationViewModel @Inject constructor(
    private val securitySettingsUseCase: ManageSecuritySettingsUseCase,
) : ViewModel() {

    fun register(pin: String) {
        viewModelScope.launch {
            securitySettingsUseCase.edit {
                it.copy(password = pin)
            }
        }
    }

    fun remove(pin: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            if (securitySettingsUseCase.settings.first().password == pin) {
                securitySettingsUseCase.edit {
                    it.copy(password = null, useBiometrics = false)
                }
                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun check(pin: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            if (securitySettingsUseCase.settings.first().password == pin) {
                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun change(pin: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            securitySettingsUseCase.edit {
                it.copy(password = pin)
            }
            onComplete()
        }
    }

    fun useBiometrics(mode: Mode, function: () -> Unit) {
        viewModelScope.launch {
            if (mode == Mode.Authentication && securitySettingsUseCase.settings.first().useBiometrics) {
                function()
            }
        }
    }
}
