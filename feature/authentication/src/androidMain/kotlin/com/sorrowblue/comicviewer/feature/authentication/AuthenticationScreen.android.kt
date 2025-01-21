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
import comicviewer.feature.authentication.generated.resources.Res
import comicviewer.feature.authentication.generated.resources.authentication_error_Invalid_pin

@PreviewMultiScreen
@Composable
private fun AuthenticationScreenPreview() {
    PreviewTheme {
        var pin by remember { mutableStateOf("1111") }
        AuthenticationScreen(
            uiState = AuthenticationScreenUiState.Authentication(
                pin,
                Res.string.authentication_error_Invalid_pin,
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
