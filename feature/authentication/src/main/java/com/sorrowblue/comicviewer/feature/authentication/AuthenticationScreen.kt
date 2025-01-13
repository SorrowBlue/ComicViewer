package com.sorrowblue.comicviewer.feature.authentication

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationColumnContents
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationContentsAction
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationRowContents
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

interface AuthenticationScreenNavigator {
    fun navigateUp()
    fun onCompleted()
}

data class AuthenticationArgs(val mode: Mode)

@Destination<ExternalModuleGraph>(navArgs = AuthenticationArgs::class)
@Composable
fun AuthenticationScreen(
    args: AuthenticationArgs,
    navigator: AuthenticationScreenNavigator,
) {
    AuthenticationScreen(
        navigator = navigator,
        state = rememberAuthenticationScreenState(args = args),
    )
}

@Composable
private fun AuthenticationScreen(
    navigator: AuthenticationScreenNavigator,
    state: AuthenticationScreenState,
) {
    AuthenticationScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onContentsAction = state::onContentsAction
    )

    val currentNavigator by rememberUpdatedState(navigator)
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEventEffect(state.event) {
        when (it) {
            AuthenticationScreenEvent.Back -> currentNavigator.navigateUp()
            AuthenticationScreenEvent.Complete -> {
                keyboardController?.hide()
                currentNavigator.onCompleted()
            }
        }
    }
}

internal sealed interface AuthenticationScreenUiState {
    val pin: String
    val error: Int
    val loading: Boolean

    fun copyPin(pin: String): AuthenticationScreenUiState

    companion object {
        fun stateSaver() = mapSaver(
            save = {
                mapOf(
                    "class" to it::class.simpleName,
                    "pin" to it.pin,
                    "error" to it.error,
                    "loading" to it.loading
                )
            },
            restore = {
                val pin = it["pin"] as String
                val error = it["error"] as Int
                val loading = it["loading"] as Boolean
                when (it.get("class")) {
                    Register.Input::class.simpleName -> {
                        Register.Input(pin, error, loading)
                    }

                    Register.Confirm::class.simpleName -> {
                        Register.Confirm(pin, error, loading)
                    }

                    Authentication::class.simpleName -> {
                        Authentication(pin, error, loading)
                    }

                    Change.ConfirmOld::class.simpleName -> {
                        Change.ConfirmOld(pin, error, loading)
                    }

                    Change.Input::class.simpleName -> {
                        Change.Input(pin, error, loading)
                    }

                    Change.Confirm::class.simpleName -> {
                        Change.Confirm(pin, error, loading)
                    }

                    Erase::class.simpleName -> {
                        Erase(pin, error, loading)
                    }

                    else -> null
                }
            }
        )
    }

    sealed interface Register : AuthenticationScreenUiState {
        data class Input(
            override val pin: String,
            override val error: Int,
            override val loading: Boolean = false,
        ) : Register {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Confirm(
            override val pin: String,
            override val error: Int,
            override val loading: Boolean = false,
        ) : Register {
            override fun copyPin(pin: String) = copy(pin = pin)
        }
    }

    data class Authentication(
        override val pin: String,
        override val error: Int,
        override val loading: Boolean = false,
    ) : AuthenticationScreenUiState {
        override fun copyPin(pin: String) = copy(pin = pin)
    }

    sealed interface Change : AuthenticationScreenUiState {
        data class ConfirmOld(
            override val pin: String,
            override val error: Int,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Input(
            override val pin: String,
            override val error: Int,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Confirm(
            override val pin: String,
            override val error: Int,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }
    }

    data class Erase(
        override val pin: String,
        override val error: Int,
        override val loading: Boolean = false,
    ) :
        AuthenticationScreenUiState {
        override fun copyPin(pin: String) = copy(pin = pin)
    }
}

@Composable
private fun AuthenticationScreen(
    uiState: AuthenticationScreenUiState,
    snackbarHostState: SnackbarHostState,
    onContentsAction: (AuthenticationContentsAction) -> Unit,
) {
    val localConfig = LocalConfiguration.current
    val isCompactWindowClass = isCompactWindowClass()
    val isCompactLandscape by remember(localConfig, isCompactWindowClass) {
        mutableStateOf(
            isCompactWindowClass && localConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        )
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ComicTheme.colorScheme.surfaceContainer,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { contentPadding ->
        if (isCompactLandscape) {
            AuthenticationRowContents(
                uiState = uiState,
                onAction = onContentsAction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(ComicTheme.dimension.margin)
            )
        } else {
            AuthenticationColumnContents(
                uiState = uiState,
                onAction = onContentsAction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(ComicTheme.dimension.margin)
            )
        }
    }
}

@PreviewMultiScreen
@Composable
private fun PreviewAuthenticationScreen() {
    PreviewTheme {
        var pin by remember { mutableStateOf("1111") }
        AuthenticationScreen(
            uiState = AuthenticationScreenUiState.Authentication(
                pin,
                R.string.authentication_error_Invalid_pin,
                true
            ),
            onContentsAction = {
                when (it) {
                    AuthenticationContentsAction.OnBackClick -> Unit
                    AuthenticationContentsAction.OnNextClick -> Unit
                    is AuthenticationContentsAction.OnPinChange -> pin = it.pin
                }
            },
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
