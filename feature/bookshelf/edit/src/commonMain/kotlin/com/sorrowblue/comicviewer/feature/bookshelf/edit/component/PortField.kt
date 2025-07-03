package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_port
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_label_port
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.compose.watch
import soil.form.rule.maximum
import soil.form.rule.minimum

@Composable
internal fun PortField(
    form: Form<SmbEditScreenForm>,
    modifier: Modifier = Modifier,
    field: FormField<Int> = form.rememberPortField(),
) {
    OutlinedTextField(
        value = if (field.value < 0) "" else field.value.toString(),
        onValueChange = { field.onValueChange(it.toIntOrNull() ?: -1) },
        label = { Text(text = field.name) },
        isError = field.hasError,
        enabled = field.isEnabled,
        supportingText = field.supportingText(),
        keyboardOptions = KeyboardOptions(
            showKeyboardOnFocus = false,
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        modifier = modifier.testTag("Port"),
    )
}

@Composable
private fun Form<SmbEditScreenForm>.rememberPortField(): FormField<Int> {
    val rangeErrorMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_port)
    return rememberField(
        name = stringResource(Res.string.bookshelf_edit_smb_input_label_port),
        selector = { it.port },
        updater = { this.copy(port = it) },
        validator = FieldValidator {
            minimum(0) { rangeErrorMessage }
            maximum(65535) { rangeErrorMessage }
        },
        enabled = watch { !value.isRunning }
    )
}
