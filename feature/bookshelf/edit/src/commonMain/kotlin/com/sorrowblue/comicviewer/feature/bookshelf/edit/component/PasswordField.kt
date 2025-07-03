package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

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
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_hint_password
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_password
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.compose.watch
import soil.form.rule.notBlank

@Composable
internal fun PasswordField(
    form: Form<SmbEditScreenForm>,
    modifier: Modifier = Modifier,
    field: FormField<String> = form.rememberPasswordField(),
) {
    var showPassword by remember { mutableStateOf(value = false) }
    OutlinedTextField(
        value = field.value,
        onValueChange = field::onValueChange,
        label = { Text(text = field.name) },
        isError = field.hasError,
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
        singleLine = true,
        modifier = modifier
            .testTag("Password")
    )
}

@Composable
private fun Form<SmbEditScreenForm>.rememberPasswordField(): FormField<String> {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_password)
    return rememberField(
        name = stringResource(Res.string.bookshelf_edit_hint_password),
        selector = { it.password },
        updater = { copy(password = it) },
        dependsOn = setOf(AuthField),
        validator = FieldValidator {
            notBlank { notBlankMessage }
        },
        enabled = watch { !value.isRunning }
    )
}
