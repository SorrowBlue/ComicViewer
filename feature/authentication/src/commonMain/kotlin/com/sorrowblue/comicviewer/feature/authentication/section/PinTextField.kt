package com.sorrowblue.comicviewer.feature.authentication.section

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.authentication.PinDrawable
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
internal fun PinTextField(
    pin: String,
    onPinChange: (String) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
                    onNextClick()
                    false
                } else if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                    true
                } else {
                    false
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        val pinCount by remember(pin) { mutableIntStateOf(pin.count()) }
        val focusRequester = remember { FocusRequester() }
        val scrollState = rememberLazyListState()
        var hasFocus by remember { mutableStateOf(false) }
        val colors = OutlinedTextFieldDefaults.colors()
        val borderColor by animateColorAsState(
            if (hasFocus) colors.focusedIndicatorColor else colors.unfocusedIndicatorColor,
        )
        TextField(
            value = pin,
            onValueChange = onPinChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next,
            ),
            colors = TextFieldDefaults.colors(),
            keyboardActions = KeyboardActions(onNext = { onNextClick() }),
            modifier = Modifier
                .size(1.dp)
                .alpha(0f)
                .onFocusChanged { hasFocus = it.hasFocus }
                .focusRequester(focusRequester),
        )
        val keyboardController = LocalSoftwareKeyboardController.current
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = if (enabled) {
                        borderColor
                    } else {
                        colors.disabledIndicatorColor
                    },
                    shape = ComicTheme.shapes.small,
                ).clickable(enabled = enabled) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                },
            state = scrollState,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(pinCount) { index ->
                PinDrawable(
                    index = index,
                    animate = index == pinCount - 1,
                    enabled = enabled,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .animateItem(),
                )
            }
        }

        LaunchedEffect(pinCount) {
            scrollState.animateScrollToItem(pinCount)
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview
@Composable
private fun PreviewPinTextField() {
    PreviewTheme {
        Column {
            var pin by remember { mutableStateOf("1111") }
            PinTextField(
                pin = pin,
                onPinChange = { pin = it },
                onNextClick = { pin = "" },
                modifier = Modifier
                    .width(240.dp)
                    .height(48.dp),
            )
        }
    }
}
