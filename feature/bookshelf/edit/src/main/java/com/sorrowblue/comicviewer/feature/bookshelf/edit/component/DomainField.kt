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
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope

@Composable
internal fun FormScope<SmbEditScreenForm>.DomainField(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberDomainFieldControl(),
) {
    Controller(control) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field.onChange,
            modifier = modifier.testTag("Domain"),
            label = { Text(text = field.name) },
            isError = field.hasError,
            enabled = enabled && field.isEnabled,
            supportingText = field.errorContent { Text(text = it.first()) },
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
        )
    }
}

@Composable
private fun FormScope<SmbEditScreenForm>.rememberDomainFieldControl(): FieldControl<String> {
    return rememberFieldControl(
        name = stringResource(R.string.bookshelf_edit_hint_domain),
        select = { domain },
        update = { copy(domain = it) },
        dependsOn = setOf(AuthField)
    )
}
