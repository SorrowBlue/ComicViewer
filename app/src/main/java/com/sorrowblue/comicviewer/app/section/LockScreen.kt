package com.sorrowblue.comicviewer.app.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.app.MainScreenUiState
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationArgs
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreen
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.Mode

@Composable
internal fun LockScreen(
    uiState: MainScreenUiState,
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    AnimatedVisibility(
        visible = uiState.isAuthenticating,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        AuthenticationScreen(
            args = AuthenticationArgs(Mode.Authentication),
            navigator = object : AuthenticationScreenNavigator {
                override fun navigateUp() = onBack()
                override fun onCompleted() = onComplete()
            }
        )
    }
}
