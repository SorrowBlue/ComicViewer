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
import com.sorrowblue.comicviewer.framework.ui.form.DigitOnlyInputTransformation
import com.sorrowblue.comicviewer.framework.ui.form.FieldValidator
import com.sorrowblue.comicviewer.framework.ui.form.Form
import com.sorrowblue.comicviewer.framework.ui.form.IntTextField
import com.sorrowblue.comicviewer.framework.ui.form.handleFocusChanged
import com.sorrowblue.comicviewer.framework.ui.form.hasError
import com.sorrowblue.comicviewer.framework.ui.form.maximum
import com.sorrowblue.comicviewer.framework.ui.form.minimum
import com.sorrowblue.comicviewer.framework.ui.form.supportingText
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_error_port
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_smb_input_label_port
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PortField(
    form: Form<SmbEditorForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val rangeErrorMessage = stringResource(Res.string.bookshelf_edit_smb_input_error_port)
    form.IntTextField(
        name = PortField,
        selector = { it.port },
        updater = { copy(port = it) },
        validator = FieldValidator {
            minimum(PortMin) { rangeErrorMessage }
            maximum(PortMax) { rangeErrorMessage }
        },
        dependsOn = setOf(HostField),
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            state = field.textFieldState,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_smb_input_label_port)) },
            isError = field.hasError,
            enabled = field.isEnabled,
            supportingText = field.supportingText(),
            inputTransformation = DigitOnlyInputTransformation,
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(PortField),
        )
    }
}

internal const val PortField = "PortField"
internal const val PortMin = 0
internal const val PortMax = 65535
