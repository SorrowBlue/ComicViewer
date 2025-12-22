package com.sorrowblue.comicviewer.feature.collection.editor.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionForm
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_error_must_not_be_blank
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_collection_name
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Field
import soil.form.compose.Form
import soil.form.compose.hasError
import soil.form.rule.notBlank

@Composable
internal fun Form<SmartCollectionForm>.CollectionNameTextField2(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val notBlankError = stringResource(Res.string.collection_editor_error_must_not_be_blank)
    Field(
        name = CollectionNameField,
        selector = { it.name },
        updater = { copy(name = it) },
        validator = FieldValidator { notBlank { notBlankError } },
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field::onValueChange,
            label = { Text(stringResource(Res.string.collection_editor_label_collection_name)) },
            isError = field.hasError,
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(CollectionNameField),
        )
    }
}

@Composable
internal fun CollectionNameTextField(
    form: Form<BasicCollectionForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val notBlankError = stringResource(Res.string.collection_editor_error_must_not_be_blank)
    form.Field(
        name = CollectionNameField,
        selector = { it.name },
        updater = { copy(name = it) },
        validator = FieldValidator { notBlank { notBlankError } },
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field::onValueChange,
            label = { Text(stringResource(Res.string.collection_editor_label_collection_name)) },
            isError = field.hasError,
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(CollectionNameField),
        )
    }
}

internal const val CollectionNameField = "CollectionNameField"
