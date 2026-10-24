/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.SmbEditorForm
import com.sorrowblue.comicviewer.framework.ui.form.FieldValidator
import com.sorrowblue.comicviewer.framework.ui.form.Form
import com.sorrowblue.comicviewer.framework.ui.form.TextField
import com.sorrowblue.comicviewer.framework.ui.form.handleFocusChanged
import com.sorrowblue.comicviewer.framework.ui.form.hasError
import com.sorrowblue.comicviewer.framework.ui.form.notBlank
import com.sorrowblue.comicviewer.framework.ui.form.supportingText
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_hint_username
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_username
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UsernameField(
    form: Form<SmbEditorForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_username)
    form.TextField(
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
            state = field.textFieldState,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_hint_username)) },
            isError =
                field.hasError ||
                    form.meta.fields[AuthField]
                        ?.error
                        ?.messages
                        ?.isNotEmpty() == true,
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(UsernameField),
        )
    }
}

internal const val UsernameField = "UsernameField"
