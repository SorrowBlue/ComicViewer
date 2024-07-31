package com.sorrowblue.comicviewer.feature.authentication.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

internal sealed interface AuthenticationContentsAction {
    data object OnBackClick : AuthenticationContentsAction
    data class OnPinChange(val pin: String) : AuthenticationContentsAction
    data object OnNextClick : AuthenticationContentsAction
}

@Composable
internal fun AuthenticationRowContents(
    uiState: AuthenticationScreenUiState,
    onAction: (AuthenticationContentsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        IconButton(onClick = { onAction(AuthenticationContentsAction.OnBackClick) }) {
            if (uiState is AuthenticationScreenUiState.Authentication) {
                Icon(imageVector = ComicIcons.Close, contentDescription = null)
            } else {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
            }
        }
        HeaderContents(modifier = Modifier.weight(1f))
        InputContents(
            uiState = uiState,
            onPinChange = { onAction(AuthenticationContentsAction.OnPinChange(it)) },
            onNextClick = { onAction(AuthenticationContentsAction.OnNextClick) },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
internal fun AuthenticationColumnContents(
    uiState: AuthenticationScreenUiState,
    onAction: (AuthenticationContentsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { onAction(AuthenticationContentsAction.OnBackClick) },
            modifier = Modifier.align(Alignment.Start)
        ) {
            if (uiState is AuthenticationScreenUiState.Authentication) {
                Icon(imageVector = ComicIcons.Close, contentDescription = null)
            } else {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
            }
        }
        HeaderContents()
        Spacer(modifier = Modifier.weight(1f))
        InputContents(
            uiState = uiState,
            onPinChange = { onAction(AuthenticationContentsAction.OnPinChange(it)) },
            onNextClick = { onAction(AuthenticationContentsAction.OnNextClick) },
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}
