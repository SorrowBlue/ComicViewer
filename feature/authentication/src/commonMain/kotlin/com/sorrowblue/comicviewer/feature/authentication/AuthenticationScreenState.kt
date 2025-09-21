package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationContentsAction
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.saveable.decodeTo
import com.sorrowblue.comicviewer.framework.ui.saveable.encodeToByteArray
import com.sorrowblue.comicviewer.framework.ui.saveable.rememberListSaveable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

internal sealed interface AuthenticationScreenEvent {
    data object Back : AuthenticationScreenEvent
    data object Complete : AuthenticationScreenEvent
}

@Composable
internal fun rememberAuthenticationScreenState(
    route: Authentication,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    biometricManager: BiometricManager = rememberBiometricManager(),
    securitySettingsUseCase: ManageSecuritySettingsUseCase = koinInject(),
): AuthenticationScreenState = rememberListSaveable(
    route,
    save = { arrayListOf(it.pinHistory, it.uiState.encodeToByteArray()) },
    restore = {
        pinHistory = it[0] as String
        uiState = (it[1] as ByteArray).decodeTo()
    }
) {
    AuthenticationScreenStateImpl(
        snackbarHostState = snackbarHostState,
        route = route,
        scope = scope,
        biometricManager = biometricManager,
        securitySettings = securitySettingsUseCase,
    )
}

internal interface AuthenticationScreenState {
    val uiState: AuthenticationScreenUiState
    val events: EventFlow<AuthenticationScreenEvent>
    val snackbarHostState: SnackbarHostState
    fun onContentsAction(action: AuthenticationContentsAction)
}

private class AuthenticationScreenStateImpl(
    route: Authentication,
    private val scope: CoroutineScope,
    private val securitySettings: ManageSecuritySettingsUseCase,
    override val snackbarHostState: SnackbarHostState,
    private val biometricManager: BiometricManager,
) : AuthenticationScreenState {

    override val events = EventFlow<AuthenticationScreenEvent>()

    override var uiState by mutableStateOf(
        when (route.screenType) {
            ScreenType.Register -> AuthenticationScreenUiState.Register.Input("")
            ScreenType.Change -> AuthenticationScreenUiState.Change.ConfirmOld("")
            ScreenType.Erase -> AuthenticationScreenUiState.Erase("")
            ScreenType.Authenticate -> AuthenticationScreenUiState.Authentication("")
        }
    )

    var pinHistory by mutableStateOf("")

    init {
        if (route.screenType == ScreenType.Authenticate && runBlocking { securitySettings.settings.first().useBiometrics }) {
            scope.launch {
                when (val result = biometricManager.authenticate()) {
                    is AuthenticationResult.Error -> snackbarHostState.showSnackbar(result.message)
                    AuthenticationResult.Success -> events.tryEmit(AuthenticationScreenEvent.Complete)
                }
            }
        }
    }

    override fun onContentsAction(action: AuthenticationContentsAction) {
        when (action) {
            AuthenticationContentsAction.BackClick -> events.tryEmit(AuthenticationScreenEvent.Back)
            AuthenticationContentsAction.NextClick -> onNextClick()
            is AuthenticationContentsAction.PinChange ->
                uiState =
                    uiState.copyPin(if (action.pin.all { it.digitToIntOrNull() != null }) action.pin else "")
        }
    }

    private fun onNextClick() {
        when (val currentUiState = uiState) {
            is AuthenticationScreenUiState.Authentication -> {
                scope.launch {
                    if (uiState.pin == securitySettings.settings.first().password) {
                        uiState = currentUiState.copy(loading = true)
                        events.tryEmit(AuthenticationScreenEvent.Complete)
                    } else {
                        uiState = AuthenticationScreenUiState.Authentication(
                            pin = "",
                            error = ErrorType.IncorrectPin
                        )
                    }
                }
            }

            is AuthenticationScreenUiState.Erase -> {
                scope.launch {
                    if (uiState.pin == securitySettings.settings.first().password) {
                        securitySettings.edit { it.copy(password = null, useBiometrics = false) }
                        uiState = currentUiState.copy(loading = true)
                        events.tryEmit(AuthenticationScreenEvent.Complete)
                    } else {
                        uiState = AuthenticationScreenUiState.Erase(
                            pin = "",
                            error = ErrorType.IncorrectPin
                        )
                    }
                }
            }

            is AuthenticationScreenUiState.Change -> onNextClickChangeScreen(currentUiState)
            is AuthenticationScreenUiState.Register -> onNextClickRegisterScreen(currentUiState)
        }
    }

    private fun onNextClickChangeScreen(currentUiState: AuthenticationScreenUiState.Change) {
        when (currentUiState) {
            is AuthenticationScreenUiState.Change.ConfirmOld -> {
                scope.launch {
                    uiState = if (uiState.pin == securitySettings.settings.first().password) {
                        AuthenticationScreenUiState.Change.Input("")
                    } else {
                        AuthenticationScreenUiState.Change.ConfirmOld(
                            pin = "",
                            error = ErrorType.IncorrectPin
                        )
                    }
                }
            }

            is AuthenticationScreenUiState.Change.Input -> {
                if (4 <= uiState.pin.count()) {
                    pinHistory = uiState.pin
                    uiState = AuthenticationScreenUiState.Change.Confirm("")
                } else {
                    uiState = AuthenticationScreenUiState.Change.Input(
                        pin = "",
                        error = ErrorType.Pin4More
                    )
                }
            }

            is AuthenticationScreenUiState.Change.Confirm -> {
                val pin = uiState.pin
                if (pin == pinHistory) {
                    scope.launch {
                        securitySettings.edit { it.copy(password = pin) }
                        uiState = currentUiState.copy(loading = true)
                        events.tryEmit(AuthenticationScreenEvent.Complete)
                    }
                } else {
                    uiState = AuthenticationScreenUiState.Change.Input(
                        pin = "",
                        error = ErrorType.IncorrectPin
                    )
                }
            }
        }
    }

    private fun onNextClickRegisterScreen(currentUiState: AuthenticationScreenUiState.Register) {
        when (currentUiState) {
            is AuthenticationScreenUiState.Register.Input -> {
                if (4 <= uiState.pin.count()) {
                    pinHistory = uiState.pin
                    uiState = AuthenticationScreenUiState.Register.Confirm("")
                } else {
                    uiState = AuthenticationScreenUiState.Register.Input(
                        pin = "",
                        error = ErrorType.Pin4More
                    )
                }
            }

            is AuthenticationScreenUiState.Register.Confirm -> {
                val pin = uiState.pin
                if (pin == pinHistory) {
                    scope.launch {
                        securitySettings.edit { it.copy(password = pin) }
                        uiState = currentUiState.copy(loading = true)
                        events.tryEmit(AuthenticationScreenEvent.Complete)
                    }
                } else {
                    pinHistory = ""
                    uiState = AuthenticationScreenUiState.Register.Input(
                        pin = "",
                        error = ErrorType.PinNotMatch
                    )
                }
            }
        }
    }
}
