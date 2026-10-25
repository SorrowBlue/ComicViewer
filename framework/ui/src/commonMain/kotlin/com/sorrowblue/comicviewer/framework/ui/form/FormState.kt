/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.form

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver

/**
 * Metadata for an individual field within [FormMeta].
 */
@Stable
interface FieldMeta {
    /**
     * The current [FieldError] for this field.
     */
    val error: FieldError
}

/**
 * Metadata for a [FormState], including overall submission readiness and field metadata.
 */
@Stable
interface FormMeta {
    /**
     * True if all currently mounted and enabled fields in the form are valid.
     */
    val canSubmit: Boolean

    /**
     * Map of field names to their [FieldMeta].
     */
    val fields: Map<FieldName, FieldMeta>
}

/**
 * Holds the state of a form of type [T].
 *
 * @param T The type of the form data model.
 * @param initialValue Initial form data value.
 */
@Stable
class FormState<T : Any>(initialValue: T) {
    internal var baseValue: T by mutableStateOf(initialValue)
    internal var isSubmitAttempted: Boolean by mutableStateOf(false)
    internal val manualErrors = mutableStateMapOf<FieldName, FieldError>()
    private val mountedFieldNames = mutableStateListOf<FieldName>()
    private val fieldRegistry = mutableMapOf<FieldName, RegisteredField<T, *>>()

    /**
     * The current value of the form, incorporating all active field states.
     */
    val value: T
        get() = fieldRegistry.values.fold(baseValue) { acc, field ->
            field.applyTo(acc)
        }

    /**
     * Metadata describing the validation state of the form and its fields.
     */
    val meta: FormMeta = object : FormMeta {
        override val canSubmit: Boolean
            get() = mountedFieldNames.isNotEmpty() &&
                mountedFieldNames.all { name ->
                    fieldRegistry[name]?.isValid() == true
                }

        override val fields: Map<FieldName, FieldMeta>
            get() = buildMap {
                fieldRegistry.forEach { (name, field) ->
                    put(name, field)
                }
                manualErrors.forEach { (name, fieldError) ->
                    if (!containsKey(name)) {
                        put(
                            name,
                            object : FieldMeta {
                                override val error: FieldError = fieldError
                            },
                        )
                    }
                }
            }
    }

    /**
     * Resets the form state to [newValue] and clears all validation errors and dirty states.
     *
     * @param newValue The new form data value.
     */
    fun reset(newValue: T) {
        baseValue = newValue
        isSubmitAttempted = false
        manualErrors.clear()
        fieldRegistry.values.forEach { field ->
            field.reset(newValue)
        }
    }

    /**
     * Manually sets validation errors on one or more fields.
     *
     * @param errors Pairs of field names and their corresponding [FieldError].
     */
    fun setError(vararg errors: Pair<FieldName, FieldError>) {
        errors.forEach { (name, error) ->
            manualErrors[name] = error
        }
    }

    internal fun onFieldEdited(name: FieldName, dependsOn: Set<FieldName>) {
        manualErrors.remove(name)
        dependsOn.forEach { dep ->
            manualErrors.remove(dep)
        }
    }

    internal fun mountField(name: FieldName) {
        if (!mountedFieldNames.contains(name)) {
            mountedFieldNames.add(name)
        }
    }

    internal fun unmountField(name: FieldName) {
        mountedFieldNames.remove(name)
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <V> getOrCreateField(
        name: FieldName,
        selector: (T) -> V,
        updater: T.(V) -> T,
        validator: ((V) -> List<String>)?,
        dependsOn: Set<FieldName>,
        enabled: Boolean,
        valueToText: ((V) -> String)? = null,
        textToValue: ((String) -> V)? = null,
    ): RegisteredField<T, V> {
        val existing = fieldRegistry[name] as? RegisteredField<T, V>
        return if (existing != null) {
            existing.selector = selector
            existing.updater = updater
            existing.validator = validator
            existing.dependsOn = dependsOn
            existing
        } else {
            RegisteredField(
                name = name,
                formState = this,
                selector = selector,
                updater = updater,
                validator = validator,
                dependsOn = dependsOn,
                enabled = enabled,
                valueToText = valueToText,
                textToValue = textToValue,
            ).also { created ->
                fieldRegistry[name] = created
            }
        }
    }
}

@Stable
internal class RegisteredField<T : Any, V>(
    override val name: FieldName,
    private val formState: FormState<T>,
    var selector: (T) -> V,
    var updater: T.(V) -> T,
    var validator: ((V) -> List<String>)?,
    var dependsOn: Set<FieldName>,
    enabled: Boolean,
    private val valueToText: ((V) -> String)? = null,
    private val textToValue: ((String) -> V)? = null,
) : TextFormField<V>,
    FieldMeta {
    override var isEnabled: Boolean by mutableStateOf(enabled)
    var isMounted: Boolean by mutableStateOf(false)
    private var isDirty: Boolean by mutableStateOf(false)
    private var hasFocused: Boolean by mutableStateOf(false)
    private var isBlurred: Boolean by mutableStateOf(false)

    private var initialText: String = valueToText?.invoke(selector(formState.baseValue)).orEmpty()
    private var lastObservedText: String = initialText

    override val textFieldState: TextFieldState = TextFieldState(initialText = initialText)

    override val value: V
        get() = if (textToValue != null) {
            textToValue.invoke(textFieldState.text.toString())
        } else {
            selector(formState.baseValue)
        }

    private val isEdited: Boolean
        get() = isDirty || (valueToText != null && textFieldState.text.toString() != initialText)

    override val error: FieldError
        get() {
            val manual = formState.manualErrors[name]
            val shouldValidate = isEnabled && (isEdited || isBlurred || formState.isSubmitAttempted)
            val messages = if (shouldValidate) validator?.invoke(value).orEmpty() else emptyList()
            return when {
                manual != null && manual.messages.isNotEmpty() -> manual
                messages.isNotEmpty() -> FieldError(messages)
                else -> FieldError.Empty
            }
        }

    fun isValid(): Boolean {
        val manual = formState.manualErrors[name]
        val hasManualError = manual != null && manual.messages.isNotEmpty()
        val isInvalidByRule = isMounted && isEnabled && !validator?.invoke(value).isNullOrEmpty()
        return !(isMounted && isEnabled && hasManualError) && !isInvalidByRule
    }

    fun applyTo(target: T): T = if (textToValue != null) {
        target.updater(value)
    } else {
        target
    }

    override fun onValueChange(value: V) {
        isDirty = true
        formState.baseValue = formState.baseValue.updater(value)
        valueToText?.invoke(value)?.let { text ->
            lastObservedText = text
            if (textFieldState.text.toString() != text) {
                textFieldState.setTextAndPlaceCursorAtEnd(text)
            }
        }
        formState.onFieldEdited(name, dependsOn)
    }

    fun onTextObserved(currentText: String) {
        if (currentText != lastObservedText) {
            lastObservedText = currentText
            isDirty = true
            textToValue?.invoke(currentText)?.let { parsed ->
                formState.baseValue = formState.baseValue.updater(parsed)
            }
            formState.onFieldEdited(name, dependsOn)
        }
    }

    override fun handleFocus(hasFocus: Boolean) {
        if (hasFocus) {
            hasFocused = true
        } else if (hasFocused) {
            isBlurred = true
        }
    }

    override fun trigger(mode: FieldValidationMode) {
        when (mode) {
            FieldValidationMode.Blur -> isBlurred = true
            FieldValidationMode.Change -> isDirty = true
            FieldValidationMode.Submit -> formState.isSubmitAttempted = true
        }
    }

    fun reset(newFormValue: T) {
        val newValue = selector(newFormValue)
        initialText = valueToText?.invoke(newValue).orEmpty()
        lastObservedText = initialText
        isDirty = false
        hasFocused = false
        isBlurred = false
        if (valueToText != null) {
            textFieldState.setTextAndPlaceCursorAtEnd(initialText)
        }
    }
}

/**
 * Remembers a [FormState] across recompositions and configuration changes.
 *
 * @param T The type of the form data model.
 * @param initialValue The initial form data value.
 * @param saver The [Saver] used to save and restore the form value.
 */
@Composable
inline fun <reified T : Any> rememberFormState(
    initialValue: T,
    saver: Saver<T, Any> = kSerializableSaver(),
): FormState<T> = rememberSaveable(
    saver = Saver(
        save = { with(saver) { save(it.value) } },
        restore = { restored -> saver.restore(restored)?.let { FormState(it) } },
    ),
) {
    FormState(initialValue)
}
