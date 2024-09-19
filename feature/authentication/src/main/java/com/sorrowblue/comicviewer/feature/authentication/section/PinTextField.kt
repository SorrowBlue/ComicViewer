package com.sorrowblue.comicviewer.feature.authentication.section

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
import androidx.compose.material3.TextField
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
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val pinCount by remember(pin) { mutableIntStateOf(pin.length) }
        val focusRequester = remember { FocusRequester() }
        val scrollState = rememberLazyListState()
        TextField(
            value = pin,
            onValueChange = onPinChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { onNextClick() }),
            modifier = Modifier
                .size(1.dp)
                .alpha(0f)
                .focusRequester(focusRequester)
        )
        val keyboardController = LocalSoftwareKeyboardController.current
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = if (enabled) {
                        ComicTheme.colorScheme.onSurfaceVariant
                    } else {
                        ComicTheme.colorScheme.onSurface.copy(
                            alpha = 0.38f
                        )
                    },
                    shape = ComicTheme.shapes.small
                )
                .clickable(enabled = enabled) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                },
            state = scrollState,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(pinCount) { index ->
                PinDrawable(
                    index = index,
                    animate = index == pinCount - 1,
                    enabled = enabled,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .animateItem()
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
                    .width(48.dp * 5)
                    .height(48.dp)
            )
        }
    }
}
