package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm
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
            factory = { context ->
                @SuppressLint("InflateParams")
                val layout =
                    LayoutInflater.from(context).inflate(R.layout.bookshelf_edit_username, null)
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
