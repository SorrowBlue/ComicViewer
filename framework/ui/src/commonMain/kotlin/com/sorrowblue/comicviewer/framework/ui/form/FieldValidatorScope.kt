/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.form

/**
 * Scope for building validation rules for a field of type [V].
 *
 * @param V The type of the field value being validated.
 */
class FieldValidatorScope<V> {
    private val rules = mutableListOf<(V) -> String?>()

    /**
     * Adds a custom validation rule.
     *
     * @param validate Function returning an error message if invalid, or null if valid.
     */
    fun rule(validate: (V) -> String?) {
        rules.add(validate)
    }

    internal fun build(): (V) -> List<String> = { value ->
        rules.mapNotNull { it(value) }
    }
}

/**
 * Creates a validator function for a form field using [FieldValidatorScope].
 *
 * @param V The type of the field value.
 * @param block Builder block defining validation rules.
 * @return A function that validates a value and returns a list of error messages.
 */
fun <V> FieldValidator(block: FieldValidatorScope<V>.() -> Unit): (V) -> List<String> =
    FieldValidatorScope<V>().apply(block).build()

/**
 * Adds a rule requiring a [String] value to not be blank.
 *
 * @param message Lazy error message provider when the string is blank.
 */
fun FieldValidatorScope<String>.notBlank(message: () -> String) {
    rule { value -> if (value.isBlank()) message() else null }
}

/**
 * Adds a rule requiring a nullable value to not be null.
 *
 * @param V The non-null value type.
 * @param message Lazy error message provider when the value is null.
 */
fun <V : Any> FieldValidatorScope<V?>.notNull(message: () -> String) {
    rule { value -> if (value == null) message() else null }
}

/**
 * Adds a rule requiring an [Int] value to be at least [min].
 *
 * @param min Minimum allowed integer value (inclusive).
 * @param message Lazy error message provider when the value is less than [min].
 */
fun FieldValidatorScope<Int>.minimum(min: Int, message: () -> String) {
    rule { value -> if (value < min) message() else null }
}

/**
 * Adds a rule requiring an [Int] value to be at most [max].
 *
 * @param max Maximum allowed integer value (inclusive).
 * @param message Lazy error message provider when the value is greater than [max].
 */
fun FieldValidatorScope<Int>.maximum(max: Int, message: () -> String) {
    rule { value -> if (value > max) message() else null }
}
