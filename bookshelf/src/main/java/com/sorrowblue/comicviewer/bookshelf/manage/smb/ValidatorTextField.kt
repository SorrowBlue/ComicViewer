package com.sorrowblue.comicviewer.bookshelf.manage.smb

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

data class ValidatorState(
    var isError: Boolean,
    val message: String?,
    val validate: (String) -> Unit,
    val revalidate: () -> Unit,
    val init: (String) -> Unit
) {
}

@Composable
fun rememberValidatorState(validate: (String) -> String?): ValidatorState {
    var message by remember { mutableStateOf<String?>(null) }
    var text by remember { mutableStateOf("") }
    return remember(message) {
        ValidatorState(
            isError = message != null,
            message = message,
            validate = {
                text = it
                message = validate.invoke(it)
            },
            revalidate = {
                message = validate.invoke(text)
            },
            init = {
                text = it
            }
        )
    }
}

@Composable
fun ValidatorTextField(
    value: String,
    label: @Composable () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    validatorState: ValidatorState = rememberValidatorState(validate = { null })
) {
    validatorState.init(value)
    OutlinedTextField(
        value = value,
        label = label,
        isError = validatorState.isError,
        onValueChange = {
            onValueChange(it)
            validatorState.validate(it)
        },
        modifier = modifier,
        prefix = prefix,
        suffix = suffix,
        supportingText = {
            if (validatorState.message != null) {
                Text(text = validatorState.message)
            }
        },
        keyboardOptions = keyboardOptions
    )
}
