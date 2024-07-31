package com.sorrowblue.comicviewer.feature.authentication

import android.content.res.Configuration
import android.os.Parcelable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.window.core.layout.WindowHeightSizeClass
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationColumnContents
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationContentsAction
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationRowContents
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo
import kotlinx.parcelize.Parcelize

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

    fun copyPin(pin: String): AuthenticationScreenUiState

    sealed interface Register : AuthenticationScreenUiState {
        data class Input(override val pin: String, override val error: Int) : Register {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Confirm(override val pin: String, override val error: Int) : Register {
            override fun copyPin(pin: String) = copy(pin = pin)
        }
    }

    @Parcelize
    data class Authentication(
        override val pin: String,
        override val error: Int,
        val loading: Boolean = false,
    ) : AuthenticationScreenUiState, Parcelable {
        override fun copyPin(pin: String) = copy(pin = pin)
    }

    sealed interface Change : AuthenticationScreenUiState {
        data class ConfirmOld(override val pin: String, override val error: Int) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Input(override val pin: String, override val error: Int) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }

        data class Confirm(override val pin: String, override val error: Int) : Change {
            override fun copyPin(pin: String) = copy(pin = pin)
        }
    }

    data class Erase(override val pin: String, override val error: Int) :
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
    val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
    val localConfig = LocalConfiguration.current
    val isCompactLandscape by remember(localConfig, windowAdaptiveInfo) {
        mutableStateOf(
            localConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && windowAdaptiveInfo.windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
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
