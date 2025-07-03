package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_hint_domain
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.compose.watch

@Composable
internal fun DomainField(
    form: Form<SmbEditScreenForm>,
    modifier: Modifier = Modifier,
    field: FormField<String> = form.rememberDomainField(),
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
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        modifier = modifier.testTag("Domain"),
    )
}

@Composable
private fun Form<SmbEditScreenForm>.rememberDomainField(): FormField<String> {
    return rememberField(
        name = stringResource(Res.string.bookshelf_edit_hint_domain),
        selector = { it.domain },
        updater = { copy(domain = it) },
        dependsOn = setOf(AuthField),
        enabled = watch { !value.isRunning }
    )
}
