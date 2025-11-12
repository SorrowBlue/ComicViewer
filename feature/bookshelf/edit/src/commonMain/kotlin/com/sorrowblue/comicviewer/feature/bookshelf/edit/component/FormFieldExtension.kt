package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import soil.form.compose.FormField
import soil.form.compose.hasError

internal fun FormField<*>.supportingText(): (@Composable () -> Unit)? = if (hasError) {
    { Text(text = error.messages.first()) }
} else {
    null
}

fun Modifier.handleFocusChanged(formField: FormField<*>): Modifier = onFocusChanged { state ->
    formField.handleFocus(state.isFocused || state.hasFocus)
}
