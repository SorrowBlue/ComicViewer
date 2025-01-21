package com.sorrowblue.comicviewer.feature.authentication

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationColumnContents
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationContentsAction
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationRowContents
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

interface AuthenticationScreenNavigator {
    fun navigateUp()
    fun onCompleted()
}

@Serializable
data class Authentication(val mode: Mode)

@Destination<Authentication>
@Composable
fun AuthenticationScreen(route: Authentication, navigator: AuthenticationScreenNavigator) {
    AuthenticationScreen(
        navigator = navigator,
        state = rememberAuthenticationScreenState(args = route),
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
    EventEffect(state.events) {
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
    val error: StringResource?
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
                val error = it["error"] as StringResource
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
            override val error: StringResource? = null,
            override val loading: Boolean = false,
        ) : Register {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Confirm(
            override val pin: String,
            override val error: StringResource? = null,
            override val loading: Boolean = false,
        ) : Register {
            override fun copyPin(pin: String) = copy(pin = pin)
        }
    }

    data class Authentication(
        override val pin: String,
        override val error: StringResource? = null,
        override val loading: Boolean = false,
    ) : AuthenticationScreenUiState {
        override fun copyPin(pin: String) = copy(pin = pin)
    }

    sealed interface Change : AuthenticationScreenUiState {
        data class ConfirmOld(
            override val pin: String,
            override val error: StringResource? = null,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Input(
            override val pin: String,
            override val error: StringResource? = null,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Confirm(
            override val pin: String,
            override val error: StringResource? = null,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }
    }

    data class Erase(
        override val pin: String,
        override val error: StringResource? = null,
        override val loading: Boolean = false,
    ) :
        AuthenticationScreenUiState {
        override fun copyPin(pin: String) = copy(pin = pin)
    }
}

@Composable
internal fun AuthenticationScreen(
    uiState: AuthenticationScreenUiState,
    snackbarHostState: SnackbarHostState,
    onContentsAction: (AuthenticationContentsAction) -> Unit,
) {
    val isCompactWindowClass = isCompactWindowClass()
    val isCompactLandscape by remember(isCompactWindowClass) {
        mutableStateOf(isCompactWindowClass)
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
