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
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_port
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_label_port
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Field
import soil.form.compose.Form
import soil.form.compose.hasError
import soil.form.rule.maximum
import soil.form.rule.minimum

@Composable
internal fun PortField(
    form: Form<SmbEditorForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val rangeErrorMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_port)
    form.Field(
        name = PortField,
        selector = { it.port },
        updater = { copy(port = it) },
        validator = FieldValidator {
            minimum(PortMin) { rangeErrorMessage }
            maximum(PortMax) { rangeErrorMessage }
        },
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            value = if (field.value < 0) "" else field.value.toString(),
            onValueChange = { field.onValueChange(it.toIntOrNull() ?: -1) },
            label = { Text(text = stringResource(Res.string.bookshelf_edit_smb_input_label_port)) },
            isError = field.hasError,
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(PortField),
        )
    }
}

internal const val PortField = "PortField"
internal const val PortMin = 0
internal const val PortMax = 65535
