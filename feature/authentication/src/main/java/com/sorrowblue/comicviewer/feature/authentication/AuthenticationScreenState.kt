package com.sorrowblue.comicviewer.feature.authentication

import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
internal interface AuthenticationScreenState : SaveableScreenState {
    val event: AuthenticationEvent
    val uiState: AuthenticationScreenUiState
    val snackbarHostState: SnackbarHostState
    fun onPinChange(pin: String)
    fun onNextClick()
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
    private val args: AuthenticationArgs,
    private val scope: CoroutineScope,
    private val viewModel: AuthenticationViewModel,
) : AuthenticationScreenState {

    override var event by mutableStateOf(AuthenticationEvent())
        private set

    val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            event = event.copy(completed = true)
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

    override fun onPinChange(pin: String) {
        uiState = uiState.copyPin(if (pin.all { it.digitToIntOrNull() != null }) pin else "")
    }

    override fun onNextClick() {
        when (uiState) {
            is AuthenticationScreenUiState.Authentication -> {
                viewModel.check(
                    uiState.pin,
                    onSuccess = {
                        uiState =
                            (uiState as AuthenticationScreenUiState.Authentication).copy(loading = true)
                        event = event.copy(completed = true)
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
                        event = event.copy(completed = true)
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
                        event = event.copy(completed = true)
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
                    event = event.copy(completed = true)
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
