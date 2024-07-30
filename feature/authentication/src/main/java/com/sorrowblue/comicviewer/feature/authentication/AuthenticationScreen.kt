package com.sorrowblue.comicviewer.feature.authentication

import android.content.res.Configuration
import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.window.core.layout.WindowHeightSizeClass
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.feature.authentication.section.HeaderContents
import com.sorrowblue.comicviewer.feature.authentication.section.InputContents
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
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
        args = args,
        onBackClick = navigator::navigateUp,
        onComplete = navigator::onCompleted
    )
}

internal data class AuthenticationEvent(
    val completed: Boolean = false,
)

@Composable
private fun AuthenticationScreen(
    args: AuthenticationArgs,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    state: AuthenticationScreenState = rememberAuthenticationScreenState(args = args),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnCompleted by rememberUpdatedState(onComplete)
    LaunchedEffect(state, lifecycle) {
        snapshotFlow { state.event }
            .filter { it.completed }
            .flowWithLifecycle(lifecycle)
            .collect {
                delay(2000)
                currentOnCompleted()
            }
    }
    AuthenticationScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onPinChange = state::onPinChange,
        onNextClick = state::onNextClick,
        snackbarHostState = state.snackbarHostState
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticationScreen(
    uiState: AuthenticationScreenUiState,
    onBackClick: () -> Unit,
    onPinChange: (String) -> Unit,
    onNextClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        topBar = {
            val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
            if (LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE ||
                windowAdaptiveInfo.windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT
            ) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            if (uiState is AuthenticationScreenUiState.Authentication) {
                                Icon(imageVector = ComicIcons.Close, contentDescription = null)
                            } else {
                                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
                            }
                        }
                    },
                    windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { contentPadding ->
        val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE &&
            windowAdaptiveInfo.windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(ComicTheme.dimension.margin)
            ) {
                IconButton(onClick = onBackClick) {
                    if (uiState is AuthenticationScreenUiState.Authentication) {
                        Icon(imageVector = ComicIcons.Close, contentDescription = null)
                    } else {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
                    }
                }
                HeaderContents(modifier = Modifier.weight(1f))
                InputContents(
                    uiState = uiState,
                    onPinChange = onPinChange,
                    onNextClick = onNextClick,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(ComicTheme.dimension.margin),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderContents(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.weight(1f))
                InputContents(
                    uiState = uiState,
                    onPinChange = onPinChange,
                    onNextClick = onNextClick,
                )
                Spacer(modifier = Modifier.weight(1f))
            }
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
            onBackClick = { /*TODO*/ },
            onPinChange = { pin = it },
            onNextClick = { /*TODO*/ },
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
