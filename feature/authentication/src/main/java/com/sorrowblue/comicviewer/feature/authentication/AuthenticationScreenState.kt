package com.sorrowblue.comicviewer.feature.authentication

import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationContentsAction
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal sealed interface AuthenticationScreenEvent {
    data object Back : AuthenticationScreenEvent
    data object Complete : AuthenticationScreenEvent
}

@Stable
internal interface AuthenticationScreenState :
    SaveableScreenState,
    ScreenStateEvent<AuthenticationScreenEvent> {
    val uiState: AuthenticationScreenUiState
    val snackbarHostState: SnackbarHostState
    fun onContentsAction(action: AuthenticationContentsAction)
}

@Composable
internal fun rememberAuthenticationScreenState(
    args: AuthenticationArgs,
    activity: FragmentActivity = LocalContext.current as FragmentActivity,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: AuthenticationViewModel = hiltViewModel(),
): AuthenticationScreenState = rememberSaveableScreenState {
    AuthenticationScreenStateImpl(
        activity = activity,
        savedStateHandle = it,
        snackbarHostState = snackbarHostState,
        args = args,
        scope = scope,
        viewModel = viewModel
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
private class AuthenticationScreenStateImpl(
    activity: FragmentActivity,
    override val savedStateHandle: SavedStateHandle,
    override val snackbarHostState: SnackbarHostState,
    override val scope: CoroutineScope,
    private val args: AuthenticationArgs,
    private val viewModel: AuthenticationViewModel,
) : AuthenticationScreenState {

    override val event = MutableSharedFlow<AuthenticationScreenEvent>()

    val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            sendEvent(AuthenticationScreenEvent.Complete)
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            scope.launch {
                snackbarHostState.showSnackbar(activity.getString(R.string.authentication_msg_auth_failed))
            }
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            scope.launch {
                snackbarHostState.showSnackbar(errString.toString())
            }
        }
    }

    init {
        viewModel.useBiometrics(args.mode) {
            val info = BiometricPrompt.PromptInfo.Builder()
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setTitle(activity.getString(R.string.authentication_title_fingerprint_auth))
                .setNegativeButtonText(activity.getString(android.R.string.cancel))
                .build()
            BiometricPrompt(activity, authenticationCallback).authenticate(info)
        }
    }

    override var uiState by savedStateHandle.saveable {
        mutableStateOf(
            when (args.mode) {
                Mode.Register -> AuthenticationScreenUiState.Register.Input("", View.NO_ID)
                Mode.Change -> AuthenticationScreenUiState.Change.ConfirmOld("", View.NO_ID)
                Mode.Erase -> AuthenticationScreenUiState.Erase("", View.NO_ID)
                Mode.Authentication -> AuthenticationScreenUiState.Authentication("", View.NO_ID)
            }
        )
    }
        private set

    private var pinHistory by savedStateHandle.saveable { mutableStateOf("") }

    override fun onContentsAction(action: AuthenticationContentsAction) {
        when (action) {
            AuthenticationContentsAction.OnBackClick -> sendEvent(AuthenticationScreenEvent.Back)
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
                        sendEvent(AuthenticationScreenEvent.Complete)
                    },
                    onError = {
                        uiState = AuthenticationScreenUiState.Authentication(
                            pin = "",
                            error = R.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Change.ConfirmOld -> {
                viewModel.check(
                    uiState.pin,
                    onSuccess = {
                        uiState = AuthenticationScreenUiState.Change.Input("", View.NO_ID)
                    },
                    onError = {
                        uiState = AuthenticationScreenUiState.Change.ConfirmOld(
                            pin = "",
                            error = R.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Change.Input -> {
                if (4 <= uiState.pin.count()) {
                    pinHistory = uiState.pin
                    uiState = AuthenticationScreenUiState.Change.Confirm("", View.NO_ID)
                } else {
                    uiState = AuthenticationScreenUiState.Change.Input(
                        pin = "",
                        error = R.string.authentication_error_pin_4_more
                    )
                }
            }

            is AuthenticationScreenUiState.Change.Confirm -> {
                if (uiState.pin == pinHistory) {
                    viewModel.change(uiState.pin) {
                        uiState = currentUiState.copy(loading = true)
                        sendEvent(AuthenticationScreenEvent.Complete)
                    }
                } else {
                    uiState = AuthenticationScreenUiState.Change.Input(
                        pin = "",
                        error = R.string.authentication_error_Invalid_pin
                    )
                }
            }

            is AuthenticationScreenUiState.Erase -> {
                viewModel.remove(
                    uiState.pin,
                    onSuccess = {
                        uiState = currentUiState.copy(loading = true)
                        sendEvent(AuthenticationScreenEvent.Complete)
                    },
                    onError = {
                        uiState = AuthenticationScreenUiState.Erase(
                            pin = "",
                            error = R.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Register.Input -> {
                if (4 <= uiState.pin.count()) {
                    pinHistory = uiState.pin
                    uiState =
                        AuthenticationScreenUiState.Register.Confirm("", View.NO_ID)
                } else {
                    uiState = AuthenticationScreenUiState.Register.Input(
                        pin = "",
                        error = R.string.authentication_error_pin_4_more
                    )
                }
            }

            is AuthenticationScreenUiState.Register.Confirm -> {
                if (uiState.pin == pinHistory) {
                    viewModel.register(uiState.pin)
                    uiState = currentUiState.copy(loading = true)
                    sendEvent(AuthenticationScreenEvent.Complete)
                } else {
                    pinHistory = ""
                    uiState = AuthenticationScreenUiState.Register.Input(
                        pin = "",
                        error = R.string.authentication_error_pin_not_match
                    )
                }
            }
        }
    }
}
