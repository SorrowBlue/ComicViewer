package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationContentsAction
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@PreviewMultiScreen
@Composable
private fun AuthenticationScreenPreview() {
    PreviewTheme {
        var pin by remember { mutableStateOf("1111") }
        AuthenticationScreen(
            uiState = AuthenticationScreenUiState.Erase(
                pin,
                ErrorType.IncorrectPin,
                true
            ),
            onContentsAction = {
                when (it) {
                    AuthenticationContentsAction.BackClick -> Unit
                    AuthenticationContentsAction.NextClick -> Unit
                    is AuthenticationContentsAction.PinChange -> pin = it.pin
                }
            },
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
