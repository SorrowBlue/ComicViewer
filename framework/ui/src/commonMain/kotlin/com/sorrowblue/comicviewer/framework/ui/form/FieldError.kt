/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.form

import androidx.compose.runtime.Immutable

/**
 * Unique identifier for a form field.
 */
typealias FieldName = String

/**
 * Represents validation errors associated with a form field.
 *
 * @property messages The list of validation error messages.
 */
@Immutable
data class FieldError(val messages: List<String> = emptyList()) {
    /**
     * Creates a [FieldError] with a single error [message].
     */
    constructor(message: String) : this(listOf(message))

    companion object {
        /**
         * An empty [FieldError] instance representing no errors.
         */
        val Empty = FieldError()
    }
}

/**
 * Triggers for form field validation.
 */
enum class FieldValidationMode {
    /** Validate when the field loses focus. */
    Blur,

    /** Validate when the field value changes. */
    Change,

    /** Validate when the form is submitted. */
    Submit,
}
