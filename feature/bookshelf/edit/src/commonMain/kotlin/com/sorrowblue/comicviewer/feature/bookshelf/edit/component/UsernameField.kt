package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

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
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_hint_username
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_username
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Field
import soil.form.compose.Form
import soil.form.compose.hasError
import soil.form.compose.watch
import soil.form.rule.notBlank

@Composable
internal fun UsernameField(
    form: Form<SmbEditForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_username)
    form.Field(
        name = UsernameField,
        selector = { it.username },
        updater = { copy(username = it) },
        validator = FieldValidator {
            notBlank { notBlankMessage }
        },
        dependsOn = setOf(AuthField),
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field::onValueChange,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_hint_username)) },
            isError =
            field.hasError ||
                form.watch {
                    meta.fields[AuthField]
                        ?.error
                        ?.messages
                        ?.isNotEmpty() == true
                },
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(UsernameField),
        )
    }
}

internal const val UsernameField = "UsernameField"
