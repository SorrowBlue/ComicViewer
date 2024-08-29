package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import soil.form.Field
import soil.form.FieldErrors
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notBlank

@Composable
internal fun <T : BookshelfEditForm> FormScope<T>.DisplayNameField(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberDisplayNameFieldControl(),
) {
    Controller(control) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field.onChange,
            modifier = modifier.testTag("DisplayName"),
            label = { Text(text = field.name) },
            isError = field.hasError,
            enabled = enabled && field.isEnabled,
            supportingText = field.errorContent { Text(text = it.first()) },
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
        )
    }
}

@Composable
private fun <T : BookshelfEditForm> FormScope<T>.rememberDisplayNameFieldControl(): FieldControl<String> {
    val notBlankMessage = stringResource(R.string.bookshelf_edit_error_display_name)
    return rememberFieldRuleControl(
        name = stringResource(R.string.bookshelf_edit_label_display_name),
        select = { displayName },
        update = { this.update(displayName = it) },
    ) {
        notBlank { notBlankMessage }
    }
}

internal fun <V> Field<V>.errorContent(content: @Composable (FieldErrors) -> Unit): @Composable (() -> Unit)? {
    return if (hasError) {
        {
            content(errors)
        }
    } else {
        null
    }
}
