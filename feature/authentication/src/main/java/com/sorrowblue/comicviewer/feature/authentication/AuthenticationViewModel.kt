package com.sorrowblue.comicviewer.feature.authentication

import android.view.View
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationArgs
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.framework.ui.lifecycle.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Stable
@HiltViewModel
internal class AuthenticationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val securitySettingsUseCase: ManageSecuritySettingsUseCase,
) : ComposeViewModel<AuthenticationUiEvent>() {

    val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            viewModelScope.launch {
                updateUiEvent(AuthenticationUiEvent.AuthCompleted)
            }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            viewModelScope.launch {
                updateUiEvent(AuthenticationUiEvent.Message("認証失敗"))
            }
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            viewModelScope.launch {
                updateUiEvent(AuthenticationUiEvent.Message(errString.toString()))
            }
        }
    }

    private val args = AuthenticationArgs(savedStateHandle)

    val handleBack = args.handleBack
    val mode = args.mode

    var uiState by mutableStateOf(
        when (args.mode) {
            Mode.Register -> AuthenticationScreenUiState.Register.Input(0, View.NO_ID)
            Mode.Change -> AuthenticationScreenUiState.Change.ConfirmOld(0, View.NO_ID)
            Mode.Erase -> AuthenticationScreenUiState.Erase(0, View.NO_ID)
            Mode.Authentication -> AuthenticationScreenUiState.Authentication(0, View.NO_ID)
        }
    )
        private set

    init {
        viewModelScope.launch {
            if (args.mode == Mode.Authentication && securitySettingsUseCase.settings.first().useBiometrics) {
                updateUiEvent(AuthenticationUiEvent.Bio)
            }
        }
    }

    fun onNextClick() {
        when (val uiState = uiState) {
            is AuthenticationScreenUiState.Authentication -> {
                viewModelScope.launch {
                    if (securitySettingsUseCase.settings.first().password == pin) {
                        updateUiEvent(AuthenticationUiEvent.AuthCompleted)
                    } else {
                        this@AuthenticationViewModel.uiState =
                            uiState.copy(error = R.string.authentication_error_Invalid_pin)
                    }
                }
            }

            is AuthenticationScreenUiState.Change.ConfirmOld -> {
                viewModelScope.launch {
                    if (securitySettingsUseCase.settings.first().password == pin) {
                        pin = ""
                        this@AuthenticationViewModel.uiState =
                            AuthenticationScreenUiState.Change.Input(pin.count(), View.NO_ID)
                    } else {
                        pin = ""
                        this@AuthenticationViewModel.uiState =
                            uiState.copy(
                                pinCount = pin.count(),
                                error = R.string.authentication_error_Invalid_pin
                            )
                    }
                }
            }

            is AuthenticationScreenUiState.Change.Input ->
                viewModelScope.launch {
                    if (4 <= pin.count()) {
                        pinHistory = pin
                        pin = ""
                        this@AuthenticationViewModel.uiState =
                            AuthenticationScreenUiState.Change.Confirm(pin.count(), View.NO_ID)
                    } else {
                        pin = ""
                        this@AuthenticationViewModel.uiState = uiState.copy(
                            pinCount = pin.count(),
                            error = R.string.authentication_error_pin_4_more
                        )
                    }
                }

            is AuthenticationScreenUiState.Change.Confirm -> {
                viewModelScope.launch {
                    if (pin == pinHistory) {
                        securitySettingsUseCase.edit {
                            it.copy(password = pin)
                        }
                        updateUiEvent(AuthenticationUiEvent.ChangeCompleted)
                    } else {
                        pin = ""
                        this@AuthenticationViewModel.uiState =
                            AuthenticationScreenUiState.Change.Input(
                                pinCount = pin.count(),
                                error = R.string.authentication_error_Invalid_pin
                            )
                    }
                }
            }

            is AuthenticationScreenUiState.Erase ->
                viewModelScope.launch {
                    if (securitySettingsUseCase.settings.first().password == pin) {
                        securitySettingsUseCase.edit {
                            it.copy(password = null, useBiometrics = false)
                        }
                        updateUiEvent(AuthenticationUiEvent.ChangeCompleted)
                    } else {
                        this@AuthenticationViewModel.uiState =
                            uiState.copy(error = R.string.authentication_error_Invalid_pin)
                    }
                }

            is AuthenticationScreenUiState.Register.Input ->
                viewModelScope.launch {
                    if (4 <= pin.count()) {
                        pinHistory = pin
                        pin = ""
                        this@AuthenticationViewModel.uiState =
                            AuthenticationScreenUiState.Register.Confirm(pin.count(), View.NO_ID)
                    } else {
                        pin = ""
                        this@AuthenticationViewModel.uiState = uiState.copy(
                            pinCount = pin.count(),
                            error = R.string.authentication_error_pin_4_more
                        )
                    }
                }

            is AuthenticationScreenUiState.Register.Confirm -> viewModelScope.launch {
                if (pin == pinHistory) {
                    securitySettingsUseCase.edit {
                        it.copy(password = pin)
                    }
                    updateUiEvent(AuthenticationUiEvent.ChangeCompleted)
                } else {
                    pin = ""
                    this@AuthenticationViewModel.uiState =
                        AuthenticationScreenUiState.Register.Input(
                            pinCount = pin.count(),
                            error = R.string.authentication_error_pin_not_match
                        )
                }
            }
        }
    }

    private var pinHistory = ""
    private var pin = ""

    fun onPinClick(pin: String) {
        this.pin += pin
        updatePinCount()
    }

    fun onBackspaceClick() {
        pin = pin.dropLast(1)
        updatePinCount()
    }

    private fun updatePinCount() {
        uiState = when (val uiState = uiState) {
            is AuthenticationScreenUiState.Authentication -> uiState.copy(pinCount = pin.count())
            is AuthenticationScreenUiState.Change.Confirm -> uiState.copy(pinCount = pin.count())
            is AuthenticationScreenUiState.Change.ConfirmOld -> uiState.copy(pinCount = pin.count())
            is AuthenticationScreenUiState.Change.Input -> uiState.copy(pinCount = pin.count())
            is AuthenticationScreenUiState.Erase -> uiState.copy(pinCount = pin.count())
            is AuthenticationScreenUiState.Register.Confirm -> uiState.copy(pinCount = pin.count())
            is AuthenticationScreenUiState.Register.Input -> uiState.copy(pinCount = pin.count())
        }
    }
}
