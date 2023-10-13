package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OutlinedTextField2(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        label = label,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .bringIntoViewRequester2(bringIntoViewRequester)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Modifier.bringIntoViewRequester2(bringIntoViewRequester: BringIntoViewRequester): Modifier {
    val scope = rememberCoroutineScope()
    return bringIntoViewRequester(bringIntoViewRequester)
        .onFocusEvent {
            if (it.isFocused) {
                scope.launch {
                    delay(300)
                    bringIntoViewRequester.bringIntoView()
                }
            }
        }
}

