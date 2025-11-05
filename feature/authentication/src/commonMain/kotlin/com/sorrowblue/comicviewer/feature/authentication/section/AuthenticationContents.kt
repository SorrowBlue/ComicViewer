package com.sorrowblue.comicviewer.feature.authentication.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
internal fun AuthenticationRowContents(
    uiState: AuthenticationScreenUiState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    onPinChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        IconButton(onClick = onBackClick) {
            if (uiState is AuthenticationScreenUiState.Authentication) {
                Icon(imageVector = ComicIcons.Close, contentDescription = null)
            } else {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
            }
        }
        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Image(
                    imageVector = ComicIcons.Launcher,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.size(ComicTheme.dimension.padding))
                Text(
                    text =
                        when (uiState) {
                            is AuthenticationScreenUiState.Authentication -> ""
                            is AuthenticationScreenUiState.Change -> "PINを変更"
                            is AuthenticationScreenUiState.Erase -> "PINを削除"
                            is AuthenticationScreenUiState.Register -> "PINを登録"
                        },
                    style = ComicTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
            }
            InputContents(
                uiState = uiState,
                onPinChange = onPinChange,
                onNextClick = onNextClick,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
internal fun AuthenticationColumnContents(
    uiState: AuthenticationScreenUiState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    onPinChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        ) {
            if (uiState is AuthenticationScreenUiState.Authentication) {
                Icon(imageVector = ComicIcons.Close, contentDescription = null)
            } else {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
            }
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                imageVector = ComicIcons.Launcher,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.size(ComicTheme.dimension.padding))
            Text(
                text =
                    when (uiState) {
                        is AuthenticationScreenUiState.Authentication -> ""
                        is AuthenticationScreenUiState.Change -> "PINを変更"
                        is AuthenticationScreenUiState.Erase -> "PINを削除"
                        is AuthenticationScreenUiState.Register -> "PINを登録"
                    },
                style = ComicTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(ComicTheme.dimension.padding * 2))
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputContents(
                uiState = uiState,
                onPinChange = onPinChange,
                onNextClick = onNextClick,
            )
        }
    }
}
