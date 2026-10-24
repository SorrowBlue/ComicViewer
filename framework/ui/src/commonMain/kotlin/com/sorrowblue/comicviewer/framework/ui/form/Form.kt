/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshotFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver

/**
 * Controller connecting a [FormState] with form submission and field compositions.
 *
 * @param T The type of the form data model.
 * @property state The underlying [FormState].
 * @property onSubmit Callback invoked with the current form value when a valid form is submitted.
 */
@Stable
class Form<T : Any>(val state: FormState<T>, private val onSubmit: (T) -> Unit) {
    /**
     * Current value of the form.
     */
    val value: T
        get() = state.value

    /**
     * Validation metadata of the form.
     */
    val meta: FormMeta
        get() = state.meta

    /**
     * Validates all fields and invokes [onSubmit] if the form can be submitted.
     */
    fun handleSubmit() {
        state.isSubmitAttempted = true
        if (state.meta.canSubmit) {
            onSubmit(state.value)
        }
    }
}

/**
 * Remembers a [Form] instance bound to an existing [FormState].
 *
 * @param T The type of the form data model.
 * @param state The [FormState] holding the form data.
 * @param onSubmit Callback invoked when the form is submitted and valid.
 */
@Composable
fun <T : Any> rememberForm(state: FormState<T>, onSubmit: (T) -> Unit): Form<T> =
    remember(state, onSubmit) {
        Form(state = state, onSubmit = onSubmit)
    }

/**
 * Remembers a [Form] instance with an internally created [FormState].
 *
 * @param T The type of the form data model.
 * @param initialValue The initial form data value.
 * @param saver The [Saver] used to persist the form data value.
 * @param onSubmit Callback invoked when the form is submitted and valid.
 */
@Composable
inline fun <reified T : Any> rememberForm(
    initialValue: T,
    saver: Saver<T, Any> = kSerializableSaver(),
    noinline onSubmit: (T) -> Unit,
): Form<T> {
    val formState = rememberFormState(initialValue = initialValue, saver = saver)
    return rememberForm(state = formState, onSubmit = onSubmit)
}

/**
 * Remembers and registers a non-text [FormField] within this [Form].
 *
 * @param T The type of the form data model.
 * @param V The type of the field value.
 * @param name Unique field name.
 * @param selector Selects the field value from the form value.
 * @param updater Returns a new form value with the updated field value.
 * @param validator Optional validation function returning error messages.
 * @param dependsOn Field names whose manual errors should be cleared when this field changes.
 * @param enabled Whether the field is enabled.
 */
@Composable
fun <T : Any, V> Form<T>.rememberField(
    name: FieldName,
    selector: (T) -> V,
    updater: T.(V) -> T,
    validator: ((V) -> List<String>)? = null,
    dependsOn: Set<FieldName> = emptySet(),
    enabled: Boolean = true,
): FormField<V> {
    val field = remember(state, name) {
        state.getOrCreateField(
            name = name,
            selector = selector,
            updater = updater,
            validator = validator,
            dependsOn = dependsOn,
            enabled = enabled,
        )
    }
    DisposableEffect(field, name) {
        field.isMounted = true
        state.mountField(name)
        onDispose {
            field.isMounted = false
            state.unmountField(name)
        }
    }
    DisposableEffect(field, enabled, validator, dependsOn) {
        field.isEnabled = enabled
        field.validator = validator
        field.dependsOn = dependsOn
        onDispose {}
    }
    return field
}

/**
 * Composes a non-text [FormField] within this [Form].
 *
 * @param T The type of the form data model.
 * @param V The type of the field value.
 * @param name Unique field name.
 * @param selector Selects the field value from the form value.
 * @param updater Returns a new form value with the updated field value.
 * @param validator Optional validation function returning error messages.
 * @param dependsOn Field names whose manual errors should be cleared when this field changes.
 * @param enabled Whether the field is enabled.
 * @param content Composable content receiving the bound [FormField].
 */
@Composable
fun <T : Any, V> Form<T>.Field(
    name: FieldName,
    selector: (T) -> V,
    updater: T.(V) -> T,
    validator: ((V) -> List<String>)? = null,
    dependsOn: Set<FieldName> = emptySet(),
    enabled: Boolean = true,
    content: @Composable (FormField<V>) -> Unit,
) {
    val field = rememberField(
        name = name,
        selector = selector,
        updater = updater,
        validator = validator,
        dependsOn = dependsOn,
        enabled = enabled,
    )
    content(field)
}

/**
 * Composes a [String]-backed [TextFormField] managed by Compose's `TextFieldState`.
 *
 * @param T The type of the form data model.
 * @param name Unique field name.
 * @param selector Selects the string value from the form value.
 * @param updater Returns a new form value with the updated string value.
 * @param validator Optional validation function returning error messages.
 * @param dependsOn Field names whose manual errors should be cleared when this field changes.
 * @param enabled Whether the field is enabled.
 * @param content Composable content receiving the bound [TextFormField].
 */
@Composable
fun <T : Any> Form<T>.TextField(
    name: FieldName,
    selector: (T) -> String,
    updater: T.(String) -> T,
    validator: ((String) -> List<String>)? = null,
    dependsOn: Set<FieldName> = emptySet(),
    enabled: Boolean = true,
    content: @Composable (TextFormField<String>) -> Unit,
) {
    val field = remember(state, name) {
        state.getOrCreateField(
            name = name,
            selector = selector,
            updater = updater,
            validator = validator,
            dependsOn = dependsOn,
            enabled = enabled,
            valueToText = { it },
            textToValue = { it },
        )
    }
    DisposableEffect(field, name) {
        field.isMounted = true
        state.mountField(name)
        onDispose {
            field.isMounted = false
            state.unmountField(name)
        }
    }
    DisposableEffect(field, enabled, validator, dependsOn) {
        field.isEnabled = enabled
        field.validator = validator
        field.dependsOn = dependsOn
        onDispose {}
    }
    LaunchedEffect(field.textFieldState) {
        snapshotFlow { field.textFieldState.text.toString() }
            .collect { currentText ->
                field.onTextObserved(currentText)
            }
    }
    content(field)
}

/**
 * Composes an [Int]-backed [TextFormField] managed by Compose's `TextFieldState`.
 *
 * @param T The type of the form data model.
 * @param name Unique field name.
 * @param selector Selects the integer value from the form value.
 * @param updater Returns a new form value with the updated integer value.
 * @param validator Optional validation function returning error messages.
 * @param dependsOn Field names whose manual errors should be cleared when this field changes.
 * @param enabled Whether the field is enabled.
 * @param invalidValue Fallback integer value when the text cannot be parsed as an [Int].
 * @param content Composable content receiving the bound [TextFormField].
 */
@Composable
fun <T : Any> Form<T>.IntTextField(
    name: FieldName,
    selector: (T) -> Int,
    updater: T.(Int) -> T,
    validator: ((Int) -> List<String>)? = null,
    dependsOn: Set<FieldName> = emptySet(),
    enabled: Boolean = true,
    invalidValue: Int = -1,
    content: @Composable (TextFormField<Int>) -> Unit,
) {
    val field = remember(state, name) {
        state.getOrCreateField(
            name = name,
            selector = selector,
            updater = updater,
            validator = validator,
            dependsOn = dependsOn,
            enabled = enabled,
            valueToText = { if (it < 0) "" else it.toString() },
            textToValue = { it.toIntOrNull() ?: invalidValue },
        )
    }
    DisposableEffect(field, name) {
        field.isMounted = true
        state.mountField(name)
        onDispose {
            field.isMounted = false
            state.unmountField(name)
        }
    }
    DisposableEffect(field, enabled, validator, dependsOn) {
        field.isEnabled = enabled
        field.validator = validator
        field.dependsOn = dependsOn
        onDispose {}
    }
    LaunchedEffect(field.textFieldState) {
        snapshotFlow { field.textFieldState.text.toString() }
            .collect { currentText ->
                field.onTextObserved(currentText)
            }
    }
    content(field)
}
