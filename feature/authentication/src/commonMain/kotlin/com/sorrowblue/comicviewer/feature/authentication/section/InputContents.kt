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
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenUiState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.authentication.generated.resources.Res
import comicviewer.feature.authentication.generated.resources.authentication_text_enter_new_pin
import comicviewer.feature.authentication.generated.resources.authentication_text_enter_pin
import comicviewer.feature.authentication.generated.resources.authentication_text_reenter_pin
import org.jetbrains.compose.resources.stringResource

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
                when (uiState) {
                    is AuthenticationScreenUiState.Authentication -> Res.string.authentication_text_enter_pin
                    is AuthenticationScreenUiState.Register.Input -> Res.string.authentication_text_enter_new_pin
                    is AuthenticationScreenUiState.Register.Confirm -> Res.string.authentication_text_reenter_pin
                    is AuthenticationScreenUiState.Change.ConfirmOld -> Res.string.authentication_text_enter_pin
                    is AuthenticationScreenUiState.Change.Input -> Res.string.authentication_text_enter_new_pin
                    is AuthenticationScreenUiState.Change.Confirm -> Res.string.authentication_text_reenter_pin
                    is AuthenticationScreenUiState.Erase -> Res.string.authentication_text_enter_pin
                }
            ),
            style = MaterialTheme.typography.titleMedium
        )

        val enabled by remember(uiState) { derivedStateOf { !uiState.loading } }
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
        AnimatedVisibility(visible = uiState.error != null) {
            if (uiState.error != null) {
                Text(
                    text = stringResource(uiState.error!!.resource),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = ComicTheme.dimension.padding)
                )
            }
        }
    }
}
