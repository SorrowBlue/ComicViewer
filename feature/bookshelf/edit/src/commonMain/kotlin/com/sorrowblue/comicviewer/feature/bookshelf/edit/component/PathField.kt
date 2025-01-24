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
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope

@Composable
internal fun FormScope<SmbEditScreenForm>.PathField(
    auth: SmbEditScreenForm.Auth,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberPathFieldControl(),
) {
    Controller(control) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field.onChange,
            modifier = modifier.testTag("Path"),
            label = { Text(text = field.name) },
            isError = field.hasError,
            prefix = { Text(text = stringResource(Res.string.bookshelf_edit_smb_input_prefix_path)) },
            suffix = { Text(text = stringResource(Res.string.bookshelf_edit_smb_input_suffix_path)) },
            enabled = enabled && field.isEnabled,
            supportingText = field.errorContent { Text(text = it.first()) },
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.Uri,
                imeAction = if (auth == SmbEditScreenForm.Auth.Guest) {
                    ImeAction.Done
                } else {
                    ImeAction.Next
                }
            ),
            singleLine = true,
        )
    }
}

@Composable
private fun FormScope<SmbEditScreenForm>.rememberPathFieldControl(): FieldControl<String> {
    return rememberFieldControl(
        name = stringResource(Res.string.bookshelf_edit_smb_input_label_path),
        select = { path },
        update = { copy(path = it) }
    )
}
