package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditForm
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_path
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_label_path
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_prefix_path
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_suffix_path
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidationMode
import soil.form.FieldValidator
import soil.form.compose.Field
import soil.form.compose.Form
import soil.form.compose.hasError
import soil.form.rule.notBlank

@Composable
internal fun PathField(
    form: Form<SmbEditForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_error_bad_path)
    form.Field(
        name = PathFieldName,
        selector = { it.path },
        updater = { copy(path = it) },
        validator = FieldValidator {
            notBlank { notBlankMessage }
        },
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field::onValueChange,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_smb_input_label_path)) },
            isError = field.hasError,
            prefix = {
                Text(
                    text = stringResource(Res.string.bookshelf_edit_smb_input_prefix_path),
                )
            },
            suffix = {
                Text(
                    text = stringResource(Res.string.bookshelf_edit_smb_input_suffix_path),
                )
            },
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.Uri,
                imeAction = if (form.value.auth == SmbEditForm.Auth.Guest) {
                    ImeAction.Done
                } else {
                    ImeAction.Next
                },
            ),
            keyboardActions = KeyboardActions(onDone = {
                // When focus doesn't move to the next field, you need to manually trigger validation
                field.trigger(FieldValidationMode.Blur)
                defaultKeyboardAction(ImeAction.Done)
            }),
            singleLine = true,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(PathFieldName),
        )
    }
}

internal const val PathFieldName = "path"
