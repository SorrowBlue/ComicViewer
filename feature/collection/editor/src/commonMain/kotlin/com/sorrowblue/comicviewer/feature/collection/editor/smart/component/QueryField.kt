package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.collection.editor.component.supportingText
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_error_must_not_be_blank
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_search_keyword
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.rule.notBlank

@Composable
internal fun QueryField(
    form: Form<SmartCollectionEditorFormData>,
    modifier: Modifier = Modifier,
    field: FormField<String> = form.rememberQueryField(),
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
private fun Form<SmartCollectionEditorFormData>.rememberQueryField(): FormField<String> {
    val errorMessage = stringResource(Res.string.collection_editor_error_must_not_be_blank)
    return rememberField(
        name = stringResource(Res.string.collection_editor_label_search_keyword),
        selector = { it.searchCondition.query },
        updater = { copy(searchCondition = searchCondition.copy(query = it)) },
        validator = FieldValidator {
            notBlank { errorMessage }
        }
    )
}
