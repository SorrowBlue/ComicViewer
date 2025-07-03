package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditForm
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_display_name
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_display_name
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.compose.watch
import soil.form.rule.notBlank

@Composable
internal fun <T : BookshelfEditForm> DisplayNameField(
    form: Form<T>,
    modifier: Modifier = Modifier,
    field: FormField<String> = form.rememberDisplayNameField(),
) {
    OutlinedTextField(
        value = field.value,
        onValueChange = field::onValueChange,
        label = { Text(text = field.name) },
        isError = field.hasError,
        enabled = field.isEnabled,
        supportingText = field.supportingText(),
        keyboardOptions = KeyboardOptions(
            showKeyboardOnFocus = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        modifier = modifier.testTag("DisplayName"),
    )
}

@Composable
private fun <T : BookshelfEditForm> Form<T>.rememberDisplayNameField(): FormField<String> {
    val notBlankMessage = stringResource(Res.string.bookshelf_edit_error_display_name)
    return rememberField(
        name = stringResource(Res.string.bookshelf_edit_label_display_name),
        selector = { it.displayName },
        updater = { this.update(displayName = it) },
        validator = FieldValidator {
            notBlank { notBlankMessage }
        },
        enabled = watch { !value.isRunning }
    )
}
