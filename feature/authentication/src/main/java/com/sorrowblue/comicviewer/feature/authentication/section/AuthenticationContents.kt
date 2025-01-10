package com.sorrowblue.comicviewer.feature.authentication.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

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

        Icon(
            imageVector = ComicIcons.Launcher,
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.size(ComicTheme.dimension.padding))
        Text(
            text =
            when (uiState) {
                is AuthenticationScreenUiState.Authentication -> "パスコードロック中"
                is AuthenticationScreenUiState.Change.Confirm -> "パスコードを変更する"
                is AuthenticationScreenUiState.Change.ConfirmOld -> "パスコードを変更する"
                is AuthenticationScreenUiState.Change.Input -> "パスコードを変更する"
                is AuthenticationScreenUiState.Erase -> "パスコードロックを無効化する"
                is AuthenticationScreenUiState.Register.Confirm -> "パスコードロックを有効化する"
                is AuthenticationScreenUiState.Register.Input -> "パスコードロックを有効化する"
            },
            style = ComicTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(1f))
        InputContents(
            uiState = uiState,
            onPinChange = { onAction(AuthenticationContentsAction.OnPinChange(it)) },
            onNextClick = { onAction(AuthenticationContentsAction.OnNextClick) },
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}
