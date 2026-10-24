/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.form

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import com.sorrowblue.comicviewer.framework.test.SmallTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SmallTest
class FormStateTest {

    private data class SampleForm(
        val name: String = "",
        val port: Int = 445,
        val path: String? = null,
    )

    @Test
    fun initialFormWithInvalidRequiredFieldsCannotSubmitAndShowsNoErrors() {
        val formState = FormState(SampleForm())
        val nameField = formState.getOrCreateField(
            name = "name",
            selector = { it.name },
            updater = { copy(name = it) },
            validator = FieldValidator { notBlank { "Name is required" } },
            dependsOn = emptySet(),
            enabled = true,
            valueToText = { it },
            textToValue = { it },
        )
        nameField.isMounted = true
        formState.mountField("name")

        assertFalse(formState.meta.canSubmit)
        assertFalse(nameField.hasError)
    }

    @Test
    fun textFieldStateUpdatesFormValueAndValidationImmediately() {
        val formState = FormState(SampleForm())
        val nameField = formState.getOrCreateField(
            name = "name",
            selector = { it.name },
            updater = { copy(name = it) },
            validator = FieldValidator { notBlank { "Name is required" } },
            dependsOn = emptySet(),
            enabled = true,
            valueToText = { it },
            textToValue = { it },
        )
        nameField.isMounted = true
        formState.mountField("name")

        nameField.textFieldState.setTextAndPlaceCursorAtEnd("My Bookshelf")
        nameField.onTextObserved(nameField.textFieldState.text.toString())

        assertEquals("My Bookshelf", formState.value.name)
        assertTrue(formState.meta.canSubmit)
        assertFalse(nameField.hasError)

        nameField.textFieldState.setTextAndPlaceCursorAtEnd("")
        nameField.onTextObserved(nameField.textFieldState.text.toString())

        assertEquals("", formState.value.name)
        assertFalse(formState.meta.canSubmit)
        assertTrue(nameField.hasError)
        assertEquals(listOf("Name is required"), nameField.error.messages)
    }

    @Test
    fun intFieldValidationAndResetWorkCorrectly() {
        val formState = FormState(SampleForm(name = "Valid"))
        val portField = formState.getOrCreateField(
            name = "port",
            selector = { it.port },
            updater = { copy(port = it) },
            validator = FieldValidator {
                minimum(0) { "Out of range" }
                maximum(65535) { "Out of range" }
            },
            dependsOn = setOf("host"),
            enabled = true,
            valueToText = { if (it < 0) "" else it.toString() },
            textToValue = { it.toIntOrNull() ?: -1 },
        )
        portField.isMounted = true
        formState.mountField("port")

        assertTrue(formState.meta.canSubmit)

        portField.textFieldState.setTextAndPlaceCursorAtEnd("70000")
        portField.onTextObserved(portField.textFieldState.text.toString())
        assertFalse(formState.meta.canSubmit)
        assertTrue(portField.hasError)

        formState.reset(SampleForm(name = "ResetName", port = 8080))
        assertEquals("8080", portField.textFieldState.text.toString())
        assertEquals(8080, formState.value.port)
        assertFalse(portField.hasError)
        assertTrue(formState.meta.canSubmit)
    }

    @Test
    fun manualErrorsAreSetAndClearedWhenDependentFieldChanges() {
        val formState = FormState(SampleForm(name = "Valid"))
        val nameField = formState.getOrCreateField(
            name = "name",
            selector = { it.name },
            updater = { copy(name = it) },
            validator = FieldValidator { notBlank { "Required" } },
            dependsOn = setOf("auth"),
            enabled = true,
            valueToText = { it },
            textToValue = { it },
        )
        nameField.isMounted = true
        formState.mountField("name")

        formState.setError(
            "name" to FieldError("Server error"),
            "auth" to FieldError("Auth error"),
        )
        assertTrue(nameField.hasError)
        assertEquals("Server error", nameField.error.messages.first())
        assertEquals("Auth error", formState.meta.fields["auth"]?.error?.messages?.first())
        assertFalse(formState.meta.canSubmit)

        nameField.textFieldState.setTextAndPlaceCursorAtEnd("Updated")
        nameField.onTextObserved(nameField.textFieldState.text.toString())

        assertFalse(nameField.hasError)
        assertEquals(null, formState.meta.fields["auth"]?.error)
        assertTrue(formState.meta.canSubmit)
    }
}
