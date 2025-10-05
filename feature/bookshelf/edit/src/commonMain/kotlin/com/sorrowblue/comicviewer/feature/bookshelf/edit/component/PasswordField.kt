package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditorForm
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_hint_password
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_password
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidationMode
import soil.form.FieldValidator
import soil.form.compose.Field
import soil.form.compose.Form
import soil.form.compose.hasError
import soil.form.compose.watch
import soil.form.rule.notBlank

@Composable
internal fun PasswordField(
    form: Form<SmbEditorForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var showPassword by remember { mutableStateOf(false) }
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_password)
    form.Field(
        name = PasswordField,
        selector = { it.password },
        updater = { copy(password = it) },
        validator = FieldValidator {
            notBlank { notBlankMessage }
        },
        dependsOn = setOf(AuthField),
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field::onValueChange,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_hint_password)) },
            isError = field.hasError || form.watch { meta.fields[AuthField]?.error?.messages?.isNotEmpty() == true },
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                if (showPassword) {
                    IconButton(onClick = { showPassword = false }) {
                        Icon(
                            imageVector = ComicIcons.Visibility,
                            contentDescription = "hide_password"
                        )
                    }
                } else {
                    IconButton(onClick = { showPassword = true }) {
                        Icon(
                            imageVector = ComicIcons.VisibilityOff,
                            contentDescription = "hide_password"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                field.trigger(FieldValidationMode.Blur)
                defaultKeyboardAction(ImeAction.Done)
            }),
            singleLine = true,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(PasswordField)
        )
    }
}

internal const val PasswordField = "PasswordField"
