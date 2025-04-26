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
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_hint_username
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_username
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notBlank

@Composable
internal fun FormScope<SmbEditScreenForm>.UsernameFieldView(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberUsernameFieldControl(),
) {
    Controller(control) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field.onChange,
            label = { Text(text = field.name) },
            isError = field.hasError,
            enabled = enabled && field.isEnabled,
            supportingText = field.errorContent { Text(text = it.first()) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = modifier
                .testTag("Username")
        )
    }
}

@Composable
private fun FormScope<SmbEditScreenForm>.rememberUsernameFieldControl(): FieldControl<String> {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_username)
    return rememberFieldRuleControl(
        name = stringResource(Res.string.bookshelf_edit_hint_username),
        select = { username },
        update = { copy(username = it) },
        dependsOn = setOf(AuthField)
    ) {
        notBlank { notBlankMessage }
    }
}
