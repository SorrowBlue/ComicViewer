package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationContentsAction
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import comicviewer.feature.authentication.generated.resources.Res
import comicviewer.feature.authentication.generated.resources.authentication_error_Invalid_pin
import comicviewer.feature.authentication.generated.resources.authentication_error_pin_4_more
import comicviewer.feature.authentication.generated.resources.authentication_error_pin_not_match
import comicviewer.feature.authentication.generated.resources.authentication_msg_auth_failed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

internal sealed interface AuthenticationScreenEvent {
    data object Back : AuthenticationScreenEvent
    data object Complete : AuthenticationScreenEvent
}

@Stable
internal interface AuthenticationScreenState : SaveableScreenState {
    val uiState: AuthenticationScreenUiState
    val events: EventFlow<AuthenticationScreenEvent>
    val snackbarHostState: SnackbarHostState
    fun onContentsAction(action: AuthenticationContentsAction)
}

@Composable
internal fun rememberAuthenticationScreenState(
    args: Authentication,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    biometricManager: BiometricManager = rememberBiometricManager(),
    viewModel: AuthenticationViewModel = koinViewModel(),
): AuthenticationScreenState = rememberSaveableScreenState {
    AuthenticationScreenStateImpl(
        savedStateHandle = it,
        snackbarHostState = snackbarHostState,
        args = args,
        scope = scope,
        biometricManager = biometricManager,
        viewModel = viewModel
    )
}

internal expect class BiometricManager {
    suspend fun authenticate(): AuthenticationResult
}

@Composable internal expect fun rememberBiometricManager(): BiometricManager

internal sealed interface AuthenticationResult {
    data object Success : AuthenticationResult
    data object Failed : AuthenticationResult
    data class Error(val message: String) : AuthenticationResult
}

@OptIn(SavedStateHandleSaveableApi::class)
private class AuthenticationScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
    private val args: Authentication,
    val biometricManager: BiometricManager,
    private val viewModel: AuthenticationViewModel,
) : AuthenticationScreenState {

    override val events = EventFlow<AuthenticationScreenEvent>()

    init {
        viewModel.useBiometrics(args.mode) {
            scope.launch {
                when (val result = biometricManager.authenticate()) {
                    AuthenticationResult.Success ->
                        events.tryEmit(AuthenticationScreenEvent.Complete)

                    AuthenticationResult.Failed ->
                        snackbarHostState.showSnackbar(getString(Res.string.authentication_msg_auth_failed))

                    is AuthenticationResult.Error ->
                        snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }

    override var uiState by savedStateHandle.saveable(stateSaver = AuthenticationScreenUiState.stateSaver()) {
        mutableStateOf(
            when (args.mode) {
                Mode.Register -> AuthenticationScreenUiState.Register.Input("")
                Mode.Change -> AuthenticationScreenUiState.Change.ConfirmOld("")
                Mode.Erase -> AuthenticationScreenUiState.Erase("")
                Mode.Authentication -> AuthenticationScreenUiState.Authentication("")
            }
        )
    }
        private set

    private var pinHistory by savedStateHandle.saveable { mutableStateOf("") }

    override fun onContentsAction(action: AuthenticationContentsAction) {
        when (action) {
            AuthenticationContentsAction.OnBackClick -> events.tryEmit(AuthenticationScreenEvent.Back)
            AuthenticationContentsAction.OnNextClick -> onNextClick()
            is AuthenticationContentsAction.OnPinChange ->
                uiState =
                    uiState.copyPin(if (action.pin.all { it.digitToIntOrNull() != null }) action.pin else "")
        }
    }

    private fun onNextClick() {
        when (val currentUiState = uiState) {
            is AuthenticationScreenUiState.Authentication -> {
                viewModel.check(
                    uiState.pin,
                    onSuccess = {
                        uiState = currentUiState.copy(loading = true)
                        events.tryEmit(AuthenticationScreenEvent.Complete)
                    },
                    onError = {
                        uiState = AuthenticationScreenUiState.Authentication(
                            pin = "",
                            error = Res.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Change.ConfirmOld -> {
                viewModel.check(
                    uiState.pin,
                    onSuccess = {
                        uiState = AuthenticationScreenUiState.Change.Input("")
                    },
                    onError = {
                        uiState = AuthenticationScreenUiState.Change.ConfirmOld(
                            pin = "",
                            error = Res.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Change.Input -> {
                if (4 <= uiState.pin.count()) {
                    pinHistory = uiState.pin
                    uiState = AuthenticationScreenUiState.Change.Confirm("")
                } else {
                    uiState = AuthenticationScreenUiState.Change.Input(
                        pin = "",
                        error = Res.string.authentication_error_pin_4_more
                    )
                }
            }

            is AuthenticationScreenUiState.Change.Confirm -> {
                if (uiState.pin == pinHistory) {
                    viewModel.change(uiState.pin) {
                        uiState = currentUiState.copy(loading = true)
                        events.tryEmit(AuthenticationScreenEvent.Complete)
                    }
                } else {
                    uiState = AuthenticationScreenUiState.Change.Input(
                        pin = "",
                        error = Res.string.authentication_error_Invalid_pin
                    )
                }
            }

            is AuthenticationScreenUiState.Erase -> {
                viewModel.remove(
                    uiState.pin,
                    onSuccess = {
                        uiState = currentUiState.copy(loading = true)
                        events.tryEmit(AuthenticationScreenEvent.Complete)
                    },
                    onError = {
                        uiState = AuthenticationScreenUiState.Erase(
                            pin = "",
                            error = Res.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Register.Input -> {
                if (4 <= uiState.pin.count()) {
                    pinHistory = uiState.pin
                    uiState =
                        AuthenticationScreenUiState.Register.Confirm("")
                } else {
                    uiState = AuthenticationScreenUiState.Register.Input(
                        pin = "",
                        error = Res.string.authentication_error_pin_4_more
                    )
                }
            }

            is AuthenticationScreenUiState.Register.Confirm -> {
                if (uiState.pin == pinHistory) {
                    viewModel.register(uiState.pin)
                    uiState = currentUiState.copy(loading = true)
                    events.tryEmit(AuthenticationScreenEvent.Complete)
                } else {
                    pinHistory = ""
                    uiState = AuthenticationScreenUiState.Register.Input(
                        pin = "",
                        error = Res.string.authentication_error_pin_not_match
                    )
                }
            }
        }
    }
}
