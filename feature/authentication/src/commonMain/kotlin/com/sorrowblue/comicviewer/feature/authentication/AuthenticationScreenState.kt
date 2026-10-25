/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.sorrowblue.comicviewer.feature.authentication.nav.ScreenType
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.form.normalizeDigits
import com.sorrowblue.comicviewer.framework.ui.saveable.rememberSaveable
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat

internal sealed interface AuthenticationScreenEvent {
    data object Complete : AuthenticationScreenEvent
}

@Suppress("ViewModelForwarding")
@Composable
internal fun rememberAuthenticationScreenState(
    screenType: ScreenType,
    viewModel: AuthenticationViewModel = metroViewModel(),
): AuthenticationScreenState {
    val snackbarHostState = remember { SnackbarHostState() }
    val pinState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val biometricManager = rememberBiometricManager()
    val pinInputFlowStateHolder =
        rememberSaveable(saver = PinInputFlowStateHolder.saver()) { PinInputFlowStateHolder() }
    val lifecycleOwner = LocalLifecycleOwner.current
    return rememberSaveable(
        screenType,
        save = { it.uiState },
        restore = { screenUiState ->
            uiState = screenUiState
        },
    ) {
        AuthenticationScreenStateImpl(
            snackbarHostState = snackbarHostState,
            pinState = pinState,
            screenType = screenType,
            scope = scope,
            biometricManager = biometricManager,
            viewModel = viewModel,
            pinInputFlowStateHolder = pinInputFlowStateHolder,
            lifecycleOwner = lifecycleOwner,
        )
    }
}

/**
 * State holder for the authentication screen.
 * Acts as a coordinator between UI state and business logic components.
 *
 * This interface follows state hoisting best practices by:
 * - Exposing UI state through [uiState] and [pinState]
 * - Providing event callbacks through [onNextClick]
 * - Managing temporary flow state through [PinInputFlowStateHolder]
 */
internal interface AuthenticationScreenState {
    val uiState: AuthenticationScreenUiState
    val pinState: TextFieldState
    val events: EventFlow<AuthenticationScreenEvent>
    val snackbarHostState: SnackbarHostState

    fun onNextClick()
}

/**
 * Implementation of [AuthenticationScreenState] that coordinates authentication flows.
 *
 * Responsibilities:
 * - Manages UI state transitions based on screen type and user actions
 * - Uses [PinInputFlowStateHolder] for temporary PIN storage during confirmation flows
 * - Handles biometric authentication integration
 * - Emits completion events when authentication succeeds
 *
 * This implementation separates concerns by:
 * - UI State Management: Handled by this class
 * - Flow State: Managed by [PinInputFlowStateHolder]
 */
internal class AuthenticationScreenStateImpl(
    screenType: ScreenType,
    private val scope: CoroutineScope,
    override val snackbarHostState: SnackbarHostState,
    private val biometricManager: BiometricManager,
    private val viewModel: AuthenticationViewModel,
    val pinInputFlowStateHolder: PinInputFlowStateHolder,
    private val lifecycleOwner: LifecycleOwner,
    override val pinState: TextFieldState = TextFieldState(),
) : AuthenticationScreenState {
    override val events = EventFlow<AuthenticationScreenEvent>()

    override var uiState by mutableStateOf(
        when (screenType) {
            ScreenType.Register -> AuthenticationScreenUiState.Register.Input()
            ScreenType.Change -> AuthenticationScreenUiState.Change.ConfirmOld()
            ScreenType.Erase -> AuthenticationScreenUiState.Erase()
            ScreenType.Authenticate -> AuthenticationScreenUiState.Authentication()
        },
    )

    private val currentPin: String
        get() = pinState.text.normalizeDigits()

    init {
        if (screenType == ScreenType.Authenticate) {
            scope.launch {
                if (viewModel.isBiometricsEnabled()) {
                    handleBiometricAuthentication()
                }
            }
        }
    }

    private suspend fun complete() {
        events.subscriptionCount.first { it > 0 }
        events.emit(AuthenticationScreenEvent.Complete)
    }

    private suspend fun handleBiometricAuthentication() {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            val result = biometricManager.authenticate()
            logcat { "#handleBiometricAuthentication result=$result" }
            when (result) {
                is AuthenticationResult.Error -> snackbarHostState.showSnackbar(result.message)
                AuthenticationResult.Success -> complete()
            }
        }
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
            if (viewModel.verifyPin(currentPin)) {
                uiState = currentUiState.copy(loading = true)
                complete()
            } else {
                pinState.clearText()
                uiState = AuthenticationScreenUiState.Authentication(
                    error = ErrorType.IncorrectPin,
                )
            }
        }
    }

    private fun handleErase(currentUiState: AuthenticationScreenUiState.Erase) {
        scope.launch {
            if (viewModel.verifyPin(currentPin)) {
                viewModel.removePin()
                uiState = currentUiState.copy(loading = true)
                complete()
            } else {
                pinState.clearText()
                uiState = AuthenticationScreenUiState.Erase(
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
            val verified = viewModel.verifyPin(currentPin)
            pinState.clearText()
            uiState = if (verified) {
                AuthenticationScreenUiState.Change.Input()
            } else {
                AuthenticationScreenUiState.Change.ConfirmOld(
                    error = ErrorType.IncorrectPin,
                )
            }
        }
    }

    private fun handleChangeInput(currentUiState: AuthenticationScreenUiState.Change.Input) {
        val pin = currentPin
        pinState.clearText()
        if (viewModel.validatePinLength(pin)) {
            pinInputFlowStateHolder.storeTemporaryPin(pin)
            uiState = AuthenticationScreenUiState.Change.Confirm()
        } else {
            uiState = currentUiState.copy(
                error = ErrorType.Pin4More,
            )
        }
    }

    private fun handleChangeConfirm(currentUiState: AuthenticationScreenUiState.Change.Confirm) {
        val pin = currentPin
        if (pinInputFlowStateHolder.verifyTemporaryPin(pin)) {
            scope.launch {
                viewModel.savePin(pin)
                uiState = currentUiState.copy(loading = true)
                complete()
            }
        } else {
            pinState.clearText()
            uiState = AuthenticationScreenUiState.Change.Input(
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
        val pin = currentPin
        pinState.clearText()
        if (viewModel.validatePinLength(pin)) {
            pinInputFlowStateHolder.storeTemporaryPin(pin)
            uiState = AuthenticationScreenUiState.Register.Confirm()
        } else {
            uiState = currentUiState.copy(
                error = ErrorType.Pin4More,
            )
        }
    }

    private fun handleRegisterConfirm(
        currentUiState: AuthenticationScreenUiState.Register.Confirm,
    ) {
        val pin = currentPin
        if (pinInputFlowStateHolder.verifyTemporaryPin(pin)) {
            scope.launch {
                viewModel.savePin(pin)
                uiState = currentUiState.copy(loading = true)
                complete()
            }
        } else {
            pinInputFlowStateHolder.clearTemporaryPin()
            pinState.clearText()
            uiState = AuthenticationScreenUiState.Register.Input(
                error = ErrorType.PinNotMatch,
            )
        }
    }
}
