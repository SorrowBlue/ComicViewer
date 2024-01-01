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
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationArgs
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
internal interface AuthenticationScreenState {
    val complete: Pair<Boolean, Mode>?
    val handleBack: Boolean
    val uiState: AuthenticationScreenUiState
    val snackbarHostState: SnackbarHostState
    fun onPinClick(pin: String)
    fun onBackspaceClick()
    fun onNextClick(onCompleted: (Boolean, Mode) -> Unit)
    fun complete2()
}

@Composable
internal fun rememberAuthenticationScreenState(
    args: AuthenticationArgs,
    savedStateHandle: SavedStateHandle,
    activity: FragmentActivity = LocalContext.current as FragmentActivity,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: AuthenticationViewModel = hiltViewModel(),
): AuthenticationScreenState = remember {
    AuthenticationScreenStateImpl(
        activity = activity,
        savedStateHandle = savedStateHandle,
        snackbarHostState = snackbarHostState,
        args = args,
        scope = scope,
        viewModel = viewModel
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
private class AuthenticationScreenStateImpl(
    activity: FragmentActivity,
    savedStateHandle: SavedStateHandle,
    override val snackbarHostState: SnackbarHostState,
    private val args: AuthenticationArgs,
    private val scope: CoroutineScope,
    private val viewModel: AuthenticationViewModel,
) : AuthenticationScreenState {

    override var complete by mutableStateOf<Pair<Boolean, Mode>?>(null)
        private set

    override fun complete2() {
        complete = null
    }

    val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            complete = args.handleBack to args.mode
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
                Mode.Register -> AuthenticationScreenUiState.Register.Input(0, View.NO_ID)
                Mode.Change -> AuthenticationScreenUiState.Change.ConfirmOld(0, View.NO_ID)
                Mode.Erase -> AuthenticationScreenUiState.Erase(0, View.NO_ID)
                Mode.Authentication -> AuthenticationScreenUiState.Authentication(0, View.NO_ID)
            }
        )
    }
        private set

    override val handleBack = args.handleBack

    private var pin by savedStateHandle.saveable { mutableStateOf("") }
    private var pinHistory by savedStateHandle.saveable { mutableStateOf("") }

    override fun onPinClick(pin: String) {
        this.pin += pin
        uiState = uiState.copyPinCount(this.pin.count())
    }

    override fun onBackspaceClick() {
        pin = pin.dropLast(1)
        uiState = uiState.copyPinCount(pin.count())
    }

    override fun onNextClick(
        onCompleted: (Boolean, Mode) -> Unit,
    ) {
        when (uiState) {
            is AuthenticationScreenUiState.Authentication -> {
                viewModel.check(
                    pin,
                    onSuccess = {
                        onCompleted(args.handleBack, args.mode)
                    },
                    onError = {
                        pin = ""
                        uiState = AuthenticationScreenUiState.Authentication(
                            pinCount = 0,
                            error = R.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Change.ConfirmOld -> {
                viewModel.check(
                    pin,
                    onSuccess = {
                        pin = ""
                        uiState = AuthenticationScreenUiState.Change.Input(pin.count(), View.NO_ID)
                    },
                    onError = {
                        pin = ""
                        uiState = AuthenticationScreenUiState.Change.ConfirmOld(
                            pinCount = pin.count(),
                            error = R.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Change.Input -> {
                if (4 <= pin.count()) {
                    pinHistory = pin
                    pin = ""
                    uiState = AuthenticationScreenUiState.Change.Confirm(pin.count(), View.NO_ID)
                } else {
                    pin = ""
                    uiState = AuthenticationScreenUiState.Change.Input(
                        pinCount = pin.count(),
                        error = R.string.authentication_error_pin_4_more
                    )
                }
            }

            is AuthenticationScreenUiState.Change.Confirm -> {
                if (pin == pinHistory) {
                    viewModel.change(pin) {
                        onCompleted(args.handleBack, args.mode)
                    }
                } else {
                    pin = ""
                    uiState = AuthenticationScreenUiState.Change.Input(
                        pinCount = pin.count(),
                        error = R.string.authentication_error_Invalid_pin
                    )
                }
            }

            is AuthenticationScreenUiState.Erase -> {
                viewModel.remove(
                    pin,
                    onSuccess = {
                        onCompleted(args.handleBack, args.mode)
                    },
                    onError = {
                        pin = ""
                        uiState = AuthenticationScreenUiState.Erase(
                            pinCount = 0,
                            error = R.string.authentication_error_Invalid_pin
                        )
                    }
                )
            }

            is AuthenticationScreenUiState.Register.Input -> {
                if (4 <= pin.count()) {
                    pinHistory = pin
                    pin = ""
                    uiState =
                        AuthenticationScreenUiState.Register.Confirm(pin.count(), View.NO_ID)
                } else {
                    pin = ""
                    uiState = AuthenticationScreenUiState.Register.Input(
                        pinCount = pin.count(),
                        error = R.string.authentication_error_pin_4_more
                    )
                }
            }

            is AuthenticationScreenUiState.Register.Confirm -> {
                if (pin == pinHistory) {
                    viewModel.register(pin)
                    onCompleted(args.handleBack, args.mode)
                } else {
                    pin = ""
                    pinHistory = ""
                    uiState = AuthenticationScreenUiState.Register.Input(
                        pinCount = pin.count(),
                        error = R.string.authentication_error_pin_not_match
                    )
                }
            }
        }
    }
}
