package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.autofill.connectNode
import com.sorrowblue.comicviewer.framework.ui.autofill.defaultFocusChangeAutoFill
import com.sorrowblue.comicviewer.framework.ui.autofill.rememberAutoFillRequestHandler
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notBlank

@Composable
internal fun FormScope<SmbEditScreenForm>.PasswordFieldView(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberPasswordFieldControl(),
) {
    Controller(control) { field ->
        AndroidView(
            modifier = modifier,
            factory = {
                @SuppressLint("InflateParams")
                val layout = LayoutInflater.from(it).inflate(R.layout.bookshelf_edit_password, null)
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
internal fun FormScope<SmbEditScreenForm>.PasswordField(
    isSubmitting: Boolean,
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberPasswordFieldControl(),
) {
    Controller(control) { field ->
        val passwordAutoFillHandler = rememberAutoFillRequestHandler(
            autofillTypes = remember { listOf(AutofillType.Password) },
            onFill = field.onChange
        )
        var visibility by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = field.value,
            onValueChange = {
                field.onChange(it)
                if (it.isEmpty()) passwordAutoFillHandler.requestVerifyManual()
            },
            modifier = modifier
                .testTag("Password")
                .connectNode(handler = passwordAutoFillHandler)
                .defaultFocusChangeAutoFill(handler = passwordAutoFillHandler),
            label = { Text(text = field.name) },
            isError = field.hasError,
            enabled = !isSubmitting && field.isEnabled,
            supportingText = field.errorContent { Text(text = it.first()) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                IconButton(onClick = { visibility = !visibility }) {
                    AnimatedContent(targetState = visibility, transitionSpec = {
                        (
                            fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                scaleIn(
                                    initialScale = 0.92f,
                                    animationSpec = tween(220, delayMillis = 90)
                                )
                            )
                            .togetherWith(
                                fadeOut(animationSpec = tween(90)) + scaleOut(
                                    targetScale = 0.92f,
                                    animationSpec = tween(220, delayMillis = 90)
                                )
                            )
                    }) {
                        if (it) {
                            Icon(
                                imageVector = ComicIcons.Visibility,
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                imageVector = ComicIcons.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                }
            },
            visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
        )
    }
}

@Composable
private fun FormScope<SmbEditScreenForm>.rememberPasswordFieldControl(): FieldControl<String> {
    val notBlankMessage = stringResource(R.string.bookshelf_edit_smb_input_error_password)
    return rememberFieldRuleControl(
        name = stringResource(R.string.bookshelf_edit_hint_password),
        select = { password },
        update = { copy(password = it) },
        dependsOn = setOf(AuthField)
    ) {
        notBlank { notBlankMessage }
    }
}
