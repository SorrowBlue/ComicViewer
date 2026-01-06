package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.saveable.decodeTo
import com.sorrowblue.comicviewer.framework.ui.saveable.encodeToByteArray
import com.sorrowblue.comicviewer.framework.ui.saveable.rememberListSaveable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal sealed interface AuthenticationScreenEvent {
    data object Complete : AuthenticationScreenEvent
}

@Composable
context(context: AuthenticationScreenContext)
internal fun rememberAuthenticationScreenState(screenType: ScreenType): AuthenticationScreenState {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val biometricManager = rememberBiometricManager()
    val pinFlowManager = remember { PinFlowManager(context.securitySettingsUseCase) }
    val pinInputFlowStateHolder = remember { PinInputFlowStateHolder() }

    return rememberListSaveable(
        screenType,
        save = {
            arrayListOf(
                it.pinInputFlowStateHolder.getTemporaryPin(),
                it.uiState.encodeToByteArray(),
            )
        },
        restore = {
            pinInputFlowStateHolder.storeTemporaryPin(it[0] as String)
            uiState = (it[1] as ByteArray).decodeTo()
        },
    ) {
        AuthenticationScreenStateImpl(
            snackbarHostState = snackbarHostState,
            screenType = screenType,
            scope = scope,
            biometricManager = biometricManager,
            pinFlowManager = pinFlowManager,
            pinInputFlowStateHolder = pinInputFlowStateHolder,
        )
    }
}

internal interface AuthenticationScreenState {
    val uiState: AuthenticationScreenUiState
    val events: EventFlow<AuthenticationScreenEvent>
    val snackbarHostState: SnackbarHostState

    fun onNextClick()

    fun onPinChange(pin: String)
}

private class AuthenticationScreenStateImpl(
    screenType: ScreenType,
    private val scope: CoroutineScope,
    override val snackbarHostState: SnackbarHostState,
    private val biometricManager: BiometricManager,
    private val pinFlowManager: PinFlowManager,
    val pinInputFlowStateHolder: PinInputFlowStateHolder,
) : AuthenticationScreenState {
    override val events = EventFlow<AuthenticationScreenEvent>()

    override var uiState by mutableStateOf(
        when (screenType) {
            ScreenType.Register -> AuthenticationScreenUiState.Register.Input("")
            ScreenType.Change -> AuthenticationScreenUiState.Change.ConfirmOld("")
            ScreenType.Erase -> AuthenticationScreenUiState.Erase("")
            ScreenType.Authenticate -> AuthenticationScreenUiState.Authentication("")
        },
    )

    init {
        if (screenType == ScreenType.Authenticate) {
            scope.launch {
                if (pinFlowManager.isBiometricsEnabled()) {
                    handleBiometricAuthentication()
                }
            }
        }
    }

    private suspend fun handleBiometricAuthentication() {
        when (val result = biometricManager.authenticate()) {
            is AuthenticationResult.Error -> snackbarHostState.showSnackbar(result.message)
            AuthenticationResult.Success -> events.tryEmit(AuthenticationScreenEvent.Complete)
        }
    }

    override fun onPinChange(pin: String) {
        uiState = uiState.copyPin(if (pin.all { it.digitToIntOrNull() != null }) pin else "")
    }

    override fun onNextClick() {
        when (val currentUiState = uiState) {
            is AuthenticationScreenUiState.Authentication -> handleAuthentication(currentUiState)
            is AuthenticationScreenUiState.Erase -> handleErase(currentUiState)
            is AuthenticationScreenUiState.Change -> handleChange(currentUiState)
            is AuthenticationScreenUiState.Register -> handleRegister(currentUiState)
        }
    }

    private fun handleAuthentication(currentUiState: AuthenticationScreenUiState.Authentication) {
        scope.launch {
            if (pinFlowManager.verifyPin(uiState.pin)) {
                uiState = currentUiState.copy(loading = true)
                events.tryEmit(AuthenticationScreenEvent.Complete)
            } else {
                uiState = AuthenticationScreenUiState.Authentication(
                    pin = "",
                    error = ErrorType.IncorrectPin,
                )
            }
        }
    }

    private fun handleErase(currentUiState: AuthenticationScreenUiState.Erase) {
        scope.launch {
            if (pinFlowManager.verifyPin(uiState.pin)) {
                pinFlowManager.removePin()
                uiState = currentUiState.copy(loading = true)
                events.tryEmit(AuthenticationScreenEvent.Complete)
            } else {
                uiState = AuthenticationScreenUiState.Erase(
                    pin = "",
                    error = ErrorType.IncorrectPin,
                )
            }
        }
    }

    private fun handleChange(currentUiState: AuthenticationScreenUiState.Change) {
        when (currentUiState) {
            is AuthenticationScreenUiState.Change.ConfirmOld -> handleChangeConfirmOld()
            is AuthenticationScreenUiState.Change.Input -> handleChangeInput(currentUiState)
            is AuthenticationScreenUiState.Change.Confirm -> handleChangeConfirm(currentUiState)
        }
    }

    private fun handleChangeConfirmOld() {
        scope.launch {
            if (pinFlowManager.verifyPin(uiState.pin)) {
                uiState = AuthenticationScreenUiState.Change.Input("")
            } else {
                uiState = AuthenticationScreenUiState.Change.ConfirmOld(
                    pin = "",
                    error = ErrorType.IncorrectPin,
                )
            }
        }
    }

    private fun handleChangeInput(currentUiState: AuthenticationScreenUiState.Change.Input) {
        if (pinFlowManager.validatePinLength(uiState.pin)) {
            pinInputFlowStateHolder.storeTemporaryPin(uiState.pin)
            uiState = AuthenticationScreenUiState.Change.Confirm("")
        } else {
            uiState = currentUiState.copy(
                pin = "",
                error = ErrorType.Pin4More,
            )
        }
    }

    private fun handleChangeConfirm(currentUiState: AuthenticationScreenUiState.Change.Confirm) {
        if (pinInputFlowStateHolder.verifyTemporaryPin(uiState.pin)) {
            scope.launch {
                pinFlowManager.savePin(uiState.pin)
                uiState = currentUiState.copy(loading = true)
                events.tryEmit(AuthenticationScreenEvent.Complete)
            }
        } else {
            uiState = AuthenticationScreenUiState.Change.Input(
                pin = "",
                error = ErrorType.IncorrectPin,
            )
        }
    }

    private fun handleRegister(currentUiState: AuthenticationScreenUiState.Register) {
        when (currentUiState) {
            is AuthenticationScreenUiState.Register.Input -> handleRegisterInput(currentUiState)
            is AuthenticationScreenUiState.Register.Confirm -> handleRegisterConfirm(currentUiState)
        }
    }

    private fun handleRegisterInput(currentUiState: AuthenticationScreenUiState.Register.Input) {
        if (pinFlowManager.validatePinLength(uiState.pin)) {
            pinInputFlowStateHolder.storeTemporaryPin(uiState.pin)
            uiState = AuthenticationScreenUiState.Register.Confirm("")
        } else {
            uiState = currentUiState.copy(
                pin = "",
                error = ErrorType.Pin4More,
            )
        }
    }

    private fun handleRegisterConfirm(currentUiState: AuthenticationScreenUiState.Register.Confirm) {
        if (pinInputFlowStateHolder.verifyTemporaryPin(uiState.pin)) {
            scope.launch {
                pinFlowManager.savePin(uiState.pin)
                uiState = currentUiState.copy(loading = true)
                events.tryEmit(AuthenticationScreenEvent.Complete)
            }
        } else {
            pinInputFlowStateHolder.clearTemporaryPin()
            uiState = AuthenticationScreenUiState.Register.Input(
                pin = "",
                error = ErrorType.PinNotMatch,
            )
        }
    }
}
