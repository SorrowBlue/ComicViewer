package com.sorrowblue.comicviewer.feature.collection.editor.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.onFocusChanged

@Composable
internal fun OutlinedTextField(
    control: FieldControl<String>,
    modifier: Modifier = Modifier,
) {
    Controller(control) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field.onChange,
            label = { Text(field.name) },
            placeholder = { Text(field.name) },
            modifier = modifier.onFocusChanged(field),
            enabled = field.isEnabled,
            isError = field.hasError,
            singleLine = true,
            supportingText =
                if (field.hasError) {
                    {
                        Text(
                            text = field.errors.first(),
                            color = ComicTheme.colorScheme.error
                        )
                    }
                } else {
                    null
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
    }
}
