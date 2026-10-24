/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.form

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

/**
 * Represents a bound form field state and its operations.
 *
 * @param V The type of the field value.
 */
@Stable
interface FormField<V> {
    /**
     * The unique name of the field.
     */
    val name: FieldName

    /**
     * The current value of the field.
     */
    val value: V

    /**
     * The current validation error of the field.
     */
    val error: FieldError

    /**
     * Whether the field is currently enabled.
     */
    val isEnabled: Boolean

    /**
     * Updates the value of the field.
     *
     * @param value The new value.
     */
    fun onValueChange(value: V)

    /**
     * Notifies the field of focus state changes.
     *
     * @param hasFocus True if the field currently has focus.
     */
    fun handleFocus(hasFocus: Boolean)

    /**
     * Manually triggers validation for the given [mode].
     *
     * @param mode The validation mode to trigger.
     */
    fun trigger(mode: FieldValidationMode)
}

/**
 * A [FormField] backed by Compose's [TextFieldState].
 *
 * @param V The type of the field value.
 */
@Stable
interface TextFormField<V> : FormField<V> {
    /**
     * The underlying [TextFieldState] for use with Compose `TextField` / `OutlinedTextField`.
     */
    val textFieldState: TextFieldState
}

/**
 * Returns true if the [FormField] currently has any validation errors.
 */
val FormField<*>.hasError: Boolean
    get() = error.messages.isNotEmpty()

/**
 * Returns a composable supporting text lambda displaying the first error message if [hasError] is true,
 * or null when there is no error.
 */
fun FormField<*>.supportingText(): (@Composable () -> Unit)? = if (hasError) {
    { Text(text = error.messages.first()) }
} else {
    null
}

/**
 * Observes focus changes on this [Modifier] and forwards them to [formField].
 *
 * @param formField The form field to notify on focus changes.
 */
fun Modifier.handleFocusChanged(formField: FormField<*>): Modifier = onFocusChanged { state ->
    formField.handleFocus(state.isFocused || state.hasFocus)
}
