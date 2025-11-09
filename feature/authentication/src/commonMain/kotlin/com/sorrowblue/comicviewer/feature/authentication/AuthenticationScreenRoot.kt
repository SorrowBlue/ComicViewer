package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.sorrowblue.comicviewer.framework.ui.EventEffect

@Composable
context(context: AuthenticationScreenContext)
fun AuthenticationScreenRoot(
    screenType: ScreenType,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val state = rememberAuthenticationScreenState(screenType)
    AuthenticationScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onNextClick = state::onNextClick,
        onPinChange = state::onPinChange,
    )

    val keyboardController = LocalSoftwareKeyboardController.current
    EventEffect(state.events) {
        when (it) {
            AuthenticationScreenEvent.Complete -> {
                keyboardController?.hide()
                onComplete()
            }
        }
    }
}
