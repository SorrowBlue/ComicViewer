/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.form

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

/**
 * An [InputTransformation] that restricts input to decimal digits, rejecting any non-digit characters.
 *
 * Full-width digits (`０`–`９`) are normalized to half-width ASCII digits (`0`–`9`) once IME
 * composition finishes via [NormalizeDigitsEffect] so that active IME composition ranges are
 * preserved on Desktop/JVM.
 */
object DigitOnlyInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (!asCharSequence().all { it.digitToIntOrNull() != null }) {
            revertAllChanges()
        }
    }
}

/**
 * Normalizes any Unicode decimal digits (such as full-width `０`–`９`) in this [CharSequence] to
 * half-width ASCII digits (`0`–`9`).
 */
fun CharSequence.normalizeDigits(): String = buildString(length) {
    for (i in 0 until this@normalizeDigits.length) {
        val ch = this@normalizeDigits[i]
        val digit = ch.digitToIntOrNull()
        if (digit != null) {
            append(('0'.code + digit).toChar())
        } else {
            append(ch)
        }
    }
}

/**
 * Normalizes non-ASCII decimal digits (e.g., full-width `０`–`９`) in [state] to half-width ASCII
 * digits (`0`–`9`) once IME [TextFieldState.composition] finishes (`composition == null`), and
 * optionally notifies [onTextChange] with the latest text.
 *
 * @param state The [TextFieldState] to observe and normalize.
 * @param onTextChange Optional callback invoked with the observed text once normalized.
 */
@Composable
fun NormalizeDigitsEffect(state: TextFieldState, onTextChange: ((String) -> Unit)? = null) {
    LaunchedEffect(state) {
        snapshotFlow { state.composition to state.text.toString() }
            .collect { (composition, text) ->
                if (composition == null) {
                    val normalized = text.normalizeDigits()
                    if (normalized != text) {
                        state.edit {
                            val previousSelection = selection
                            replace(0, length, normalized)
                            selection = previousSelection
                        }
                        return@collect
                    }
                }
                onTextChange?.invoke(text)
            }
    }
}

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
