/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame

class ResourceTest {

    private data object SampleError : Resource.AppError

    @Test
    fun success_holdsDataAndReturnsDataOrNull() {
        val resource: Resource<String, SampleError> = Resource.Success("comic")

        assertEquals("comic", resource.dataOrNull())
    }

    @Test
    fun error_holdsErrorAndReturnsNullFromDataOrNull() {
        val resource: Resource<String, SampleError> = Resource.Error(SampleError)

        assertNull(resource.dataOrNull())
    }

    @Test
    fun fold_executesOnSuccessForSuccessResource() {
        val resource: Resource<Int, SampleError> = Resource.Success(42)

        val result = resource.fold(
            onSuccess = { "value:$it" },
            onError = { "error:$it" },
        )

        assertEquals("value:42", result)
    }

    @Test
    fun fold_executesOnErrorForErrorResource() {
        val resource: Resource<Int, SampleError> = Resource.Error(SampleError)

        val result = resource.fold(
            onSuccess = { "value:$it" },
            onError = { "error" },
        )

        assertEquals("error", result)
    }

    @Test
    fun onSuccess_invokesCallbackOnlyWhenSuccessAndReturnsSameInstance() {
        val success: Resource<String, SampleError> = Resource.Success("ok")
        val error: Resource<String, SampleError> = Resource.Error(SampleError)

        var capturedSuccess: String? = null
        val returnedFromSuccess = success.onSuccess { capturedSuccess = it }

        var errorCallbackInvoked = false
        val returnedFromError = error.onSuccess { errorCallbackInvoked = true }

        assertEquals("ok", capturedSuccess)
        assertSame(success, returnedFromSuccess)
        assertFalse(errorCallbackInvoked)
        assertSame(error, returnedFromError)
    }

    @Test
    fun onError_invokesCallbackOnlyWhenErrorAndReturnsSameInstance() {
        val success: Resource<String, SampleError> = Resource.Success("ok")
        val error: Resource<String, SampleError> = Resource.Error(SampleError)

        var successErrorCallbackInvoked = false
        val returnedFromSuccess = success.onError { successErrorCallbackInvoked = true }

        var capturedError: SampleError? = null
        val returnedFromError = error.onError { capturedError = it }

        assertFalse(successErrorCallbackInvoked)
        assertSame(success, returnedFromSuccess)
        assertEquals(SampleError, capturedError)
        assertSame(error, returnedFromError)
    }

    @Test
    fun systemError_wrapsThrowable() {
        val exception = IllegalStateException("failure")
        val systemError = Resource.SystemError(exception)

        assertSame(exception, systemError.throwable)
        assertEquals(Resource.SystemError(exception), systemError)
    }
}
