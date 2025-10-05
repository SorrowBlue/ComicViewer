package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditorForm
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_host
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_label_host
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Field
import soil.form.compose.Form
import soil.form.compose.hasError
import soil.form.rule.notBlank

@Composable
internal fun HostField(
    form: Form<SmbEditorForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_host)
    form.Field(
        name = HostField,
        selector = { it.host },
        updater = { copy(host = it) },
        validator = FieldValidator {
            notBlank { notBlankMessage }
        },
        enabled = enabled
    ) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field::onValueChange,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_smb_input_label_host)) },
            isError = field.hasError,
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(HostField),
        )
    }
}

internal const val HostField = "HostField"
