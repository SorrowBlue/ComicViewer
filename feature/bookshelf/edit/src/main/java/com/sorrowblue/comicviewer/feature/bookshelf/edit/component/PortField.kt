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
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.IntRuleBuilder
import soil.form.rule.IntRuleTester

@Composable
internal fun FormScope<SmbEditScreenForm>.PortField(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<Int> = rememberPortFieldControl(),
) {
    Controller(control) { field ->
        OutlinedTextField(
            value = if (field.value < 0) "" else field.value.toString(),
            onValueChange = { field.onChange.invoke(it.toIntOrNull() ?: -1) },
            label = { Text(text = field.name) },
            isError = field.hasError,
            enabled = enabled && field.isEnabled,
            supportingText = field.errorContent { Text(text = it.first()) },
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = false,
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = modifier.testTag("Port"),
        )
    }
}

@Composable
private fun FormScope<SmbEditScreenForm>.rememberPortFieldControl(): FieldControl<Int> {
    val rangeErrorMessage = stringResource(R.string.bookshelf_edit_smb_input_error_port)
    return rememberFieldRuleControl(
        name = stringResource(R.string.bookshelf_edit_smb_input_label_port),
        select = { port },
        update = { copy(port = it) }
    ) {
        range(0..65535) { rangeErrorMessage }
    }
}

private fun IntRuleBuilder.range(range: IntRange, message: () -> String) {
    extend(IntRuleTester({ this in range }, message))
}
