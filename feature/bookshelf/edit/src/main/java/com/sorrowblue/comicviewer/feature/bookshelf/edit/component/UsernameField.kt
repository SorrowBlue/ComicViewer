package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm
import com.sorrowblue.comicviewer.framework.ui.autofill.connectNode
import com.sorrowblue.comicviewer.framework.ui.autofill.defaultFocusChangeAutoFill
import com.sorrowblue.comicviewer.framework.ui.autofill.rememberAutoFillRequestHandler
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notBlank

@Composable
internal fun FormScope<SmbEditScreenForm>.UsernameFieldView(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberUsernameFieldControl(),
) {
    Controller(control) { field ->
        AndroidView(
            modifier = modifier,
            factory = {
                @SuppressLint("InflateParams")
                val layout = LayoutInflater.from(it).inflate(R.layout.bookshelf_edit_username, null)
                val textInputLayout = layout.findViewById<TextInputLayout>(R.id.textField)
                layout.findViewById<TextInputEditText>(R.id.editText).apply {
                    doAfterTextChanged {
                        field.onChange(it?.toString().orEmpty())
                    }
                    setText(field.value)
                }
                textInputLayout
            },
            update = {
                it.isEnabled = enabled && field.isEnabled
                it.isErrorEnabled = field.hasError
                field.errors.firstOrNull().let(it::setError)
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun FormScope<SmbEditScreenForm>.UsernameField(
    isSubmitting: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberUsernameFieldControl(),
) {
    Controller(control) { field ->
        val usernameAutoFillHandler = rememberAutoFillRequestHandler(
            autofillTypes = remember { listOf(AutofillType.Username) },
            onFill = field.onChange
        )
        OutlinedTextField(
            value = field.value,
            onValueChange = {
                field.onChange(it)
                if (it.isEmpty()) usernameAutoFillHandler.requestVerifyManual()
            },
            modifier = modifier
                .testTag("Username")
                .connectNode(handler = usernameAutoFillHandler)
                .defaultFocusChangeAutoFill(handler = usernameAutoFillHandler),
            label = { Text(text = field.name) },
            isError = field.hasError,
            enabled = !isSubmitting && field.isEnabled,
            supportingText = field.errorContent { Text(text = it.first()) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
        )
    }
}

@Composable
private fun FormScope<SmbEditScreenForm>.rememberUsernameFieldControl(): FieldControl<String> {
    val notBlankMessage = stringResource(R.string.bookshelf_edit_smb_input_error_username)
    return rememberFieldRuleControl(
        name = stringResource(R.string.bookshelf_edit_hint_username),
        select = { username },
        update = { copy(username = it) },
        dependsOn = setOf(AuthField)
    ) {
        notBlank { notBlankMessage }
    }
}
