package com.sorrowblue.comicviewer.feature.authentication

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.CoreNavigator
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filter

interface AuthenticationScreenNavigator : CoreNavigator {
    fun onBack()
    fun onCompleted()
}

data class AuthenticationArgs(val mode: Mode)

@Destination(navArgsDelegate = AuthenticationArgs::class)
@Composable
fun AuthenticationScreen(
    args: AuthenticationArgs,
    navigator: AuthenticationScreenNavigator,
) {
    AuthenticationScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onBack = navigator::onBack,
        onCompleted = navigator::onCompleted
    )
}

internal data class AuthenticationEvent(
    val completed: Boolean = false,
)

@Composable
private fun AuthenticationScreen(
    args: AuthenticationArgs,
    onBackClick: () -> Unit,
    onBack: () -> Unit,
    onCompleted: () -> Unit,
    state: AuthenticationScreenState = rememberAuthenticationScreenState(args = args),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnCompleted by rememberUpdatedState(onCompleted)
    LaunchedEffect(state, lifecycle) {
        snapshotFlow { state.event }
            .filter { it.completed }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnCompleted()
            }
    }
    AuthenticationScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onPinClick = state::onPinClick,
        onBackspaceClick = state::onBackspaceClick,
        onNextClick = state::onNextClick,
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

    data class Authentication(
        override val pinCount: Int,
        override val error: Int,
        val loading: Boolean = false,
    ) : AuthenticationScreenUiState {
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
                    if (uiState is AuthenticationScreenUiState.Authentication) {
                        TextButton(onClick = onBackClick) {
                            Text(text = "Quit App")
                        }
                    } else {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
                        }
                    }
                },
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
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
            if (uiState is AuthenticationScreenUiState.Authentication && uiState.loading) {
                CircularProgressIndicator()
            } else {
                IconButton(onClick = onNextClick) {
                    Icon(imageVector = ComicIcons.ArrowForward, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(Modifier.padding(ComicTheme.dimension.margin))
            val list = remember {
                listOf(
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "",
                    "0",
                    null
                ).toPersistentList()
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp)
            ) {
                items(list) {
                    FilledTonalButton(
                        onClick = {
                            if (it != null) {
                                onPinClick(it.toString())
                            } else {
                                onBackspaceClick()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        if (it != null) {
                            if (it.isNotEmpty()) {
                                Text(text = it)
                            }
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
