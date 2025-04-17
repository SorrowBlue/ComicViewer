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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationColumnContents
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationContentsAction
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationRowContents
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.core.isLandscape
import comicviewer.feature.authentication.generated.resources.Res
import comicviewer.feature.authentication.generated.resources.authentication_error_incorrect_pin
import comicviewer.feature.authentication.generated.resources.authentication_error_pin_4_more
import comicviewer.feature.authentication.generated.resources.authentication_error_pin_not_match
import kotlinx.serialization.Serializable
import logcat.logcat
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.koinInject

interface AuthenticationScreenNavigator {
    fun navigateUp()
    fun onCompleted()
}

@Serializable
data class Authentication(val screenType: ScreenType)

@Destination<Authentication>
@Composable
fun AuthenticationScreen(
    route: Authentication,
    navigator: AuthenticationScreenNavigator = koinInject(),
) {
    AuthenticationScreen(
        navigator = navigator,
        state = rememberAuthenticationScreenState(route = route),
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

@Serializable
internal sealed interface AuthenticationScreenUiState {
    val pin: String
    val error: ErrorType?
    val loading: Boolean

    fun copyPin(pin: String): AuthenticationScreenUiState

    @Serializable
    sealed interface Register : AuthenticationScreenUiState {
        @Serializable
        data class Input(
            override val pin: String,
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Register {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        @Serializable
        data class Confirm(
            override val pin: String,
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Register {
            override fun copyPin(pin: String) = copy(pin = pin)
        }
    }

    @Serializable
    data class Authentication(
        override val pin: String,
        override val error: ErrorType? = null,
        override val loading: Boolean = false,
    ) : AuthenticationScreenUiState {
        override fun copyPin(pin: String) = copy(pin = pin)
    }

    @Serializable
    sealed interface Change : AuthenticationScreenUiState {
        @Serializable
        data class ConfirmOld(
            override val pin: String,
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        @Serializable
        data class Input(
            override val pin: String,
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        @Serializable
        data class Confirm(
            override val pin: String,
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }
    }

    @Serializable
    data class Erase(
        override val pin: String,
        override val error: ErrorType? = null,
        override val loading: Boolean = false,
    ) : AuthenticationScreenUiState {
        override fun copyPin(pin: String) = copy(pin = pin)
    }
}

internal enum class ErrorType(val resource: StringResource) {
    IncorrectPin(Res.string.authentication_error_incorrect_pin),
    Pin4More(Res.string.authentication_error_pin_4_more),
    PinNotMatch(Res.string.authentication_error_pin_not_match),
}

@Composable
internal fun AuthenticationScreen(
    uiState: AuthenticationScreenUiState,
    snackbarHostState: SnackbarHostState,
    onContentsAction: (AuthenticationContentsAction) -> Unit,
) {
    val isCompactWindowClass = isCompactWindowClass()
    val isLandscape = isLandscape()
    val isCompactLandscape by remember(isCompactWindowClass, isLandscape) {
        logcat("AuthenticationScreen") { "isCompactWindowClass=$isCompactWindowClass, isLandscape=$isLandscape" }
        mutableStateOf(isCompactWindowClass && isLandscape)
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
