package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.framework.ui.EventEffect

@Composable
context(context: AuthenticationScreenContext)
fun AuthenticationScreenRoot(
    screenType: ScreenType,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberAuthenticationScreenState(screenType)
    AuthenticationScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onNextClick = state::onNextClick,
        onPinChange = state::onPinChange,
        modifier = modifier.testTag("AuthenticationScreenRoot"),
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
