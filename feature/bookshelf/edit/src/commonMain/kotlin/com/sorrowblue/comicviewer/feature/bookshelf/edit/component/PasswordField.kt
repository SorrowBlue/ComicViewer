/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
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
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.SmbEditorForm
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.form.FieldValidationMode
import com.sorrowblue.comicviewer.framework.ui.form.FieldValidator
import com.sorrowblue.comicviewer.framework.ui.form.Form
import com.sorrowblue.comicviewer.framework.ui.form.TextField
import com.sorrowblue.comicviewer.framework.ui.form.handleFocusChanged
import com.sorrowblue.comicviewer.framework.ui.form.hasError
import com.sorrowblue.comicviewer.framework.ui.form.notBlank
import com.sorrowblue.comicviewer.framework.ui.form.supportingText
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_hint_password
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_password
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PasswordField(
    form: Form<SmbEditorForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var showPassword by remember { mutableStateOf(false) }
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_password)
    form.TextField(
        name = PasswordField,
        selector = { it.password },
        updater = { copy(password = it) },
        validator = FieldValidator {
            notBlank { notBlankMessage }
        },
        dependsOn = setOf(AuthField),
        enabled = enabled,
    ) { field ->
        OutlinedSecureTextField(
            state = field.textFieldState,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_hint_password)) },
            isError =
                field.hasError ||
                    form.meta.fields[AuthField]
                        ?.error
                        ?.messages
                        ?.isNotEmpty() == true,
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            textObfuscationMode = if (showPassword) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.System
            },
            trailingIcon = {
                if (showPassword) {
                    IconButton(onClick = { showPassword = false }) {
                        Icon(
                            imageVector = ComicIcons.Visibility,
                            contentDescription = "hide_password",
                        )
                    }
                } else {
                    IconButton(onClick = { showPassword = true }) {
                        Icon(
                            imageVector = ComicIcons.VisibilityOff,
                            contentDescription = "hide_password",
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            onKeyboardAction = { performDefaultAction ->
                field.trigger(FieldValidationMode.Blur)
                performDefaultAction()
            },
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(PasswordField),
        )
    }
}

internal const val PasswordField = "PasswordField"
