package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditorForm
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_hint_domain
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Field
import soil.form.compose.Form
import soil.form.compose.hasError
import soil.form.compose.watch

@Composable
internal fun DomainField(
    form: Form<SmbEditorForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    form.Field(
        name = DomainField,
        selector = { it.domain },
        updater = { copy(domain = it) },
        dependsOn = setOf(AuthField),
        enabled = enabled,
    ) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field::onValueChange,
            label = { Text(text = stringResource(Res.string.bookshelf_edit_hint_domain)) },
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
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
            modifier = modifier
                .handleFocusChanged(field)
                .testTag(DomainField),
        )
    }
}

internal const val DomainField = "DomainField"
