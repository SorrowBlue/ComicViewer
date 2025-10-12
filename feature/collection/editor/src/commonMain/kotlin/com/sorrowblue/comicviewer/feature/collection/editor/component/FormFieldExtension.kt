package com.sorrowblue.comicviewer.feature.collection.editor.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import soil.form.compose.FormField
import soil.form.compose.hasError

internal fun FormField<*>.supportingText(): (@Composable () -> Unit)? {
    return if (hasError) {
        { Text(text = error.messages.first()) }
    } else {
        null
    }
}

fun Modifier.handleFocusChanged(formField: FormField<*>): Modifier {
    return onFocusChanged { state -> formField.handleFocus(state.isFocused || state.hasFocus) }
}
