package com.sorrowblue.comicviewer.feature.authentication.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenUiState
import com.sorrowblue.comicviewer.feature.authentication.R
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun InputContents(
    uiState: AuthenticationScreenUiState,
    onPinChange: (String) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        val enabled by remember(
            uiState
        ) { derivedStateOf { !(uiState is AuthenticationScreenUiState.Authentication && uiState.loading) } }
        PinTextField(
            pin = uiState.pin,
            onPinChange = onPinChange,
            onNextClick = onNextClick,
            enabled = enabled,
            modifier = Modifier
                .padding(top = ComicTheme.dimension.padding)
                .width(48.dp * 5)
                .height(48.dp)
        )
        if (uiState is AuthenticationScreenUiState.Authentication && uiState.loading) {
            LinearProgressIndicator()
        }
        AnimatedVisibility(visible = 0 < uiState.error) {
            if (0 < uiState.error) {
                Text(
                    text = stringResource(id = uiState.error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = ComicTheme.dimension.padding)
                )
            }
        }
    }
}
