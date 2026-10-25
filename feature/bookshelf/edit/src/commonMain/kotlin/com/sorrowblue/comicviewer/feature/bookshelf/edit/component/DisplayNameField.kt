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
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.BookshelfEditorForm
import com.sorrowblue.comicviewer.framework.ui.form.FieldValidator
import com.sorrowblue.comicviewer.framework.ui.form.Form
import com.sorrowblue.comicviewer.framework.ui.form.TextField
import com.sorrowblue.comicviewer.framework.ui.form.handleFocusChanged
import com.sorrowblue.comicviewer.framework.ui.form.hasError
import com.sorrowblue.comicviewer.framework.ui.form.notBlank
import com.sorrowblue.comicviewer.framework.ui.form.supportingText
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_display_name
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_display_name
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun <T : BookshelfEditorForm> DisplayNameField(
    form: Form<T>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_error_display_name)
    form.TextField(
        name = DisplayNameField,
        selector = { it.displayName },
        updater = { update(displayName = it) },
        validator = FieldValidator {
            notBlank { notBlankMessage }
        },
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            state = field.textFieldState,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_label_display_name)) },
            isError = field.hasError,
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(DisplayNameField),
        )
    }
}

internal const val DisplayNameField = "DisplayNameField"
