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
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_host
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_label_host
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notBlank

@Composable
internal fun FormScope<SmbEditScreenForm>.HostField(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberHostFieldControl(),
) {
    Controller(control) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field.onChange,
            modifier = modifier.testTag("Host"),
            label = { Text(text = field.name) },
            isError = field.hasError,
            enabled = enabled && field.isEnabled,
            supportingText = field.errorContent { Text(text = it.first()) },
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
        )
    }
}

@Composable
private fun FormScope<SmbEditScreenForm>.rememberHostFieldControl(): FieldControl<String> {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_host)
    return rememberFieldRuleControl(
        name = stringResource(Res.string.bookshelf_edit_smb_input_label_host),
        select = { host },
        update = { copy(host = it) }
    ) {
        notBlank { notBlankMessage }
    }
}
