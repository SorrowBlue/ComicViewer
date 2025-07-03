package com.sorrowblue.comicviewer.feature.collection.editor.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import soil.form.compose.FormField
import soil.form.compose.hasError

internal fun FormField<*>.supportingText(): (@Composable () -> Unit)? {
    return if (hasError) {
        { Text(text = error.messages.first()) }
    } else {
        null
    }
}
