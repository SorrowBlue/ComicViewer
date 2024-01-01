package com.sorrowblue.comicviewer.feature.authentication

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationArgs
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Destination(navArgsDelegate = AuthenticationArgs::class)
@Composable
internal fun AuthenticationScreen(
    args: AuthenticationArgs,
    savedStateHandle: SavedStateHandle,
    onBack: () -> Unit,
    onBackClick: () -> Unit,
    onAuthCompleted: (Boolean, Mode) -> Unit,
    state: AuthenticationScreenState = rememberAuthenticationScreenState(args, savedStateHandle),
) {
    if (state.complete != null) {
        val (handleBack, mode) = state.complete!!
        state.complete2()
        onAuthCompleted(handleBack, mode)
        return
    }
    AuthenticationScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onPinClick = state::onPinClick,
        onBackspaceClick = state::onBackspaceClick,
        onNextClick = { state.onNextClick(onAuthCompleted) },
        snackbarHostState = state.snackbarHostState
    )

    BackHandler(enabled = state.handleBack, onBack = onBack)
}

internal sealed interface AuthenticationScreenUiState {
    val pinCount: Int
    val error: Int

    fun copyPinCount(count: Int): AuthenticationScreenUiState

    sealed interface Register : AuthenticationScreenUiState {
        data class Input(override val pinCount: Int, override val error: Int) : Register {
            override fun copyPinCount(count: Int) = copy(pinCount = count)
        }

        data class Confirm(override val pinCount: Int, override val error: Int) : Register {
            override fun copyPinCount(count: Int) = copy(pinCount = count)
        }
    }

    data class Authentication(override val pinCount: Int, override val error: Int) :
        AuthenticationScreenUiState {
        override fun copyPinCount(count: Int) = copy(pinCount = count)
    }

    sealed interface Change : AuthenticationScreenUiState {
        data class ConfirmOld(override val pinCount: Int, override val error: Int) : Change {
            override fun copyPinCount(count: Int) = copy(pinCount = count)
        }

        data class Input(override val pinCount: Int, override val error: Int) : Change {
            override fun copyPinCount(count: Int) = copy(pinCount = count)
        }

        data class Confirm(override val pinCount: Int, override val error: Int) : Change {
            override fun copyPinCount(count: Int) = copy(pinCount = count)
        }
    }

    data class Erase(override val pinCount: Int, override val error: Int) :
        AuthenticationScreenUiState {
        override fun copyPinCount(count: Int) = copy(pinCount = count)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticationScreen(
    uiState: AuthenticationScreenUiState,
    onBackClick: () -> Unit,
    onPinClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onNextClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (uiState !is AuthenticationScreenUiState.Authentication) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
                        }
                    }
                },
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = ComicIcons.Key,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = stringResource(
                    id = when (uiState) {
                        is AuthenticationScreenUiState.Authentication -> R.string.authentication_text_enter_pin

                        is AuthenticationScreenUiState.Register.Input -> R.string.authentication_text_enter_new_pin
                        is AuthenticationScreenUiState.Register.Confirm -> R.string.authentication_text_reenter_pin

                        is AuthenticationScreenUiState.Change.ConfirmOld -> R.string.authentication_text_enter_pin
                        is AuthenticationScreenUiState.Change.Input -> R.string.authentication_text_enter_new_pin
                        is AuthenticationScreenUiState.Change.Confirm -> R.string.authentication_text_reenter_pin

                        is AuthenticationScreenUiState.Erase -> R.string.authentication_text_enter_pin
                    }
                ),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.size(16.dp))
            AnimatedVisibility(visible = 0 < uiState.error) {
                if (0 < uiState.error) {
                    Text(
                        text = stringResource(id = uiState.error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row {
                repeat(uiState.pinCount + 1) {
                    key(it) {
                        AnimatedContent(
                            targetState = it < uiState.pinCount,
                            transitionSpec = {
                                (fadeIn() + slideInVertically { height -> height }) togetherWith (fadeOut() + slideOutVertically { height -> height })
                            },
                            label = "test"
                        ) { isVisible ->
                            if (isVisible) {
                                Icon(imageVector = ComicIcons.Circle, contentDescription = null)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            IconButton(onClick = onNextClick) {
                Icon(imageVector = ComicIcons.ArrowForward, contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(Modifier.padding(ComicTheme.dimension.margin))
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", null).chunked(3)
                .forEach { chunk ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        chunk.forEach {
                            TextButton(
                                onClick = {
                                    if (it != null) {
                                        onPinClick(it.toString())
                                    } else {
                                        onBackspaceClick()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                if (it != null) {
                                    Text(text = it)
                                } else {
                                    Icon(
                                        imageVector = ComicIcons.Backspace,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}
