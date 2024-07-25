package com.sorrowblue.comicviewer.feature.authentication

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
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

    @Parcelize
    data class Authentication(
        override val pinCount: Int,
        override val error: Int,
        val loading: Boolean = false,
    ) : AuthenticationScreenUiState, Parcelable {
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
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
        FlowColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TopContent(
                uiState = uiState,
                modifier = Modifier
                    .weight(1f)
                    .align(alignment = Alignment.CenterHorizontally)
            )

            NumberPad(
                onPinClick = onPinClick,
                onBackspaceClick = onBackspaceClick,
                onNextClick = onNextClick,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun TopContent(
    uiState: AuthenticationScreenUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = ComicIcons.Key,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
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
        Spacer(modifier = Modifier.size(8.dp))

        Row {
            repeat(uiState.pinCount + 1) {
                key(it) {
                    AnimatedContent(
                        targetState = it < uiState.pinCount,
                        transitionSpec = {
                            fadeIn() + slideInVertically { height -> height } togetherWith
                                fadeOut() + slideOutVertically { height -> height } using
                                SizeTransform(false)
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
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun NumberPad(
    onPinClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.sizeIn(maxWidth = 60.dp * 3 + 16.dp, maxHeight = 60.dp * 4 + 32.dp)
    ) {
        items(Button.listList) {
            FilledTonalButton(
                onClick = {
                    when (it) {
                        Button.Delete -> onBackspaceClick()
                        Button.Next -> onNextClick()
                        is Button.Number -> onPinClick(it.value.toString())
                    }
                },
                modifier = Modifier.aspectRatio(1f)
            ) {
                when (it) {
                    Button.Delete -> {
                        Icon(imageVector = ComicIcons.Backspace, contentDescription = null)
                    }

                    Button.Next -> {
                        Icon(
                            imageVector = ComicIcons.ArrowForward,
                            contentDescription = null
                        )
                    }

                    is Button.Number -> {
                        Text(text = it.value.toString())
                    }
                }
            }
        }
    }
}

sealed interface Button {
    data class Number(val value: Int) : Button
    data object Delete : Button
    data object Next : Button

    companion object {
        val listList = List(9) { Number(it + 1) } + Delete + Number(0) + Next
    }
}

@Preview(name = "Phone", device = Devices.PHONE)
@Preview(
    name = "Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420"
)
@Preview(name = "Unfolded Foldable", device = Devices.FOLDABLE)
@Preview(name = "Tablet", device = Devices.TABLET)
@Preview(name = "Desktop", device = Devices.DESKTOP)
@Composable
private fun PreviewAuthenticationScreen() {
    PreviewTheme {
        AuthenticationScreen(
            uiState = AuthenticationScreenUiState.Authentication(4, 0, false),
            onBackClick = { /*TODO*/ },
            onPinClick = {},
            onBackspaceClick = { /*TODO*/ },
            onNextClick = { /*TODO*/ },
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
