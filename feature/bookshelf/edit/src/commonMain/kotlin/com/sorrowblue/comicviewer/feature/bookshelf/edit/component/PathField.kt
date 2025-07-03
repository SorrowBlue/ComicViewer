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
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_label_path
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_prefix_path
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_suffix_path
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.compose.watch

@Composable
internal fun PathField(
    form: Form<SmbEditScreenForm>,
    modifier: Modifier = Modifier,
    field: FormField<String> = form.rememberPathField(),
) {
    OutlinedTextField(
        value = field.value,
        onValueChange = field::onValueChange,
        label = { Text(text = field.name) },
        isError = field.hasError,
        prefix = { Text(text = stringResource(Res.string.bookshelf_edit_smb_input_prefix_path)) },
        suffix = { Text(text = stringResource(Res.string.bookshelf_edit_smb_input_suffix_path)) },
        enabled = field.isEnabled,
        supportingText = field.supportingText(),
        keyboardOptions = KeyboardOptions(
            showKeyboardOnFocus = false,
            keyboardType = KeyboardType.Uri,
            imeAction = if (form.value.auth == SmbEditScreenForm.Auth.Guest) {
                ImeAction.Done
            } else {
                ImeAction.Next
            }
        ),
        singleLine = true,
        modifier = modifier.testTag("Path"),
    )
}

@Composable
private fun Form<SmbEditScreenForm>.rememberPathField(): FormField<String> {
    return rememberField(
        name = stringResource(Res.string.bookshelf_edit_smb_input_label_path),
        selector = { it.path },
        updater = { copy(path = it) },
        enabled = watch { !value.isRunning }
    )
}
