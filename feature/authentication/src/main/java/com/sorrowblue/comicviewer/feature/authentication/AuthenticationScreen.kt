package com.sorrowblue.comicviewer.feature.authentication

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.lifecycle.LaunchedEffectUiEvent
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

internal sealed interface AuthenticationUiEvent {

    data class Message(val errString: String? = null, val errorRes: Int = View.NO_ID) :
        AuthenticationUiEvent

    data object AuthCompleted : AuthenticationUiEvent
    data object ChangeCompleted : AuthenticationUiEvent
    data object Bio : AuthenticationUiEvent
}

internal sealed interface AuthenticationScreenUiState {
    val pinCount: Int
    val error: Int

    sealed interface Register : AuthenticationScreenUiState {
        data class Input(override val pinCount: Int, override val error: Int) : Register
        data class Confirm(override val pinCount: Int, override val error: Int) : Register
    }

    data class Authentication(override val pinCount: Int, override val error: Int) :
        AuthenticationScreenUiState

    sealed interface Change : AuthenticationScreenUiState {
        data class ConfirmOld(override val pinCount: Int, override val error: Int) : Change
        data class Input(override val pinCount: Int, override val error: Int) : Change
        data class Confirm(override val pinCount: Int, override val error: Int) : Change
    }

    data class Erase(override val pinCount: Int, override val error: Int) :
        AuthenticationScreenUiState
}

@Composable
internal fun AuthenticationRoute(
    onBack: () -> Unit,
    onAuthCompleted: (Boolean, Mode) -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val activity = LocalContext.current as FragmentActivity
    val snackbarHostState = remember { SnackbarHostState() }

    AuthenticationScreen(
        viewModel.uiState,
        snackbarHostState = snackbarHostState,
        viewModel::onPinClick,
        viewModel::onBackspaceClick,
        viewModel::onNextClick
    )

    BackHandler(enabled = viewModel.handleBack, onBack = onBack)

    LaunchedEffectUiEvent(viewModel.uiEvents, viewModel::consumeUiEvent) {
        when (it) {
            AuthenticationUiEvent.AuthCompleted -> onAuthCompleted(
                viewModel.handleBack,
                viewModel.mode
            )

            AuthenticationUiEvent.ChangeCompleted -> onAuthCompleted(
                viewModel.handleBack,
                viewModel.mode
            )

            AuthenticationUiEvent.Bio -> {
                val info = BiometricPrompt.PromptInfo.Builder()
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                    .setTitle(activity.getString(R.string.authentication_title_fingerprint_auth))
                    .setNegativeButtonText(activity.getString(android.R.string.cancel))
                    .build()
                BiometricPrompt(activity, viewModel.authenticationCallback).authenticate(info)
            }

            is AuthenticationUiEvent.Message -> {
                snackbarHostState.showSnackbar(
                    message = it.errString ?: activity.getString(it.errorRes)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticationScreen(
    uiState: AuthenticationScreenUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onValueChange: (String) -> Unit = {},
    onValueRemove: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
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
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding),
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
            AnimatedVisibility(visible = uiState.error != View.NO_ID) {
                if (uiState.error != View.NO_ID) {
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
                                        onValueChange(it.toString())
                                    } else {
                                        onValueRemove()
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

@Preview(showSystemUi = false, showBackground = false)
@Composable
private fun PreviewAuthenticationScreen() {
    PreviewTheme {
        Surface {
            AuthenticationScreen(
                uiState = AuthenticationScreenUiState.Authentication(
                    4,
                    R.string.authentication_error_Invalid_pin
                )
            )
        }
    }
}
