package com.sorrowblue.comicviewer.feature.collection.editor.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.CollectionEditorFormData
import soil.form.FieldValidator
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.rule.notBlank

@Composable
internal fun CollectionNameTextField(
    form: Form<out CollectionEditorFormData>,
    modifier: Modifier = Modifier,
    field: FormField<String> = form.rememberCollectionNameField(),
) {
    OutlinedTextField(
        value = field.value,
        onValueChange = field::onValueChange,
        label = { Text(field.name) },
        placeholder = { Text(field.name) },
        modifier = modifier.onFocusChanged { field.handleFocus(it.hasFocus) },
        enabled = field.isEnabled,
        isError = field.hasError,
        singleLine = true,
        supportingText = field.supportingText(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        )
    )
}

@Composable
private fun <T : CollectionEditorFormData> Form<T>.rememberCollectionNameField(): FormField<String> {
    return rememberField(
        name = "Collection name",
        selector = { it.name },
        updater = { update(name = it) },
        validator = FieldValidator {
            notBlank { "must not be blank" }
        }
    )
}
