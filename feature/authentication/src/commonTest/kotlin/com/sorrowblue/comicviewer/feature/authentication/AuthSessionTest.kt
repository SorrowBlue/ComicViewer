/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class AuthSessionTest {

    private lateinit var testScope: TestScope
    private lateinit var fakeUseCase: FakeManageSecuritySettingsUseCase
    private lateinit var authSession: AuthSession

    @BeforeTest
    fun setup() {
        testScope = TestScope()
        fakeUseCase = FakeManageSecuritySettingsUseCase()
    }

    private fun createAuthSession(): AuthSession = AuthSession(
        manageSecuritySettingsUseCase = fakeUseCase,
        scope = testScope.backgroundScope,
    )

    @Test
    fun `initial state - with password requires lock`() = testScope.runTest {
        fakeUseCase.flow.value = SecuritySettings(password = "1234")
        authSession = createAuthSession()
        runCurrent()

        assertTrue(authSession.authRequired.value)
        assertEquals(AuthState.AuthRequired(authed = false), authSession.authState.value)
    }

    @Test
    fun `initial state - without password does not require lock`() = testScope.runTest {
        fakeUseCase.flow.value = SecuritySettings(password = null)
        authSession = createAuthSession()
        runCurrent()

        assertFalse(authSession.authRequired.value)
        assertEquals(AuthState.NoAuthRequired, authSession.authState.value)
    }

    @Test
    fun `onAuthComplete - unlocks session`() = testScope.runTest {
        fakeUseCase.flow.value = SecuritySettings(password = "1234")
        authSession = createAuthSession()
        runCurrent()

        assertEquals(AuthState.AuthRequired(authed = false), authSession.authState.value)

        authSession.onAuthComplete()
        runCurrent()

        assertEquals(AuthState.AuthRequired(authed = true), authSession.authState.value)
    }

    @Test
    fun `onPause - locks session when lockOnBackground is true`() = testScope.runTest {
        fakeUseCase.flow.value = SecuritySettings(password = "1234", lockOnBackground = true)
        authSession = createAuthSession()
        runCurrent()

        authSession.onAuthComplete()
        runCurrent()
        assertEquals(AuthState.AuthRequired(authed = true), authSession.authState.value)

        authSession.onPause()
        runCurrent()
        assertEquals(AuthState.AuthRequired(authed = false), authSession.authState.value)
    }

    @Test
    fun `onPause - does not lock session when lockOnBackground is false`() = testScope.runTest {
        fakeUseCase.flow.value = SecuritySettings(password = "1234", lockOnBackground = false)
        authSession = createAuthSession()
        runCurrent()

        authSession.onAuthComplete()
        runCurrent()
        assertEquals(AuthState.AuthRequired(authed = true), authSession.authState.value)

        authSession.onPause()
        runCurrent()
        assertEquals(AuthState.AuthRequired(authed = true), authSession.authState.value)
    }

    @Test
    fun `onPause - does not lock session when password is not required`() = testScope.runTest {
        fakeUseCase.flow.value = SecuritySettings(password = null, lockOnBackground = true)
        authSession = createAuthSession()
        runCurrent()

        assertEquals(AuthState.NoAuthRequired, authSession.authState.value)

        authSession.onPause()
        runCurrent()
        assertEquals(AuthState.NoAuthRequired, authSession.authState.value)
    }

    @Test
    fun `password added during session - stays unlocked`() = testScope.runTest {
        fakeUseCase.flow.value = SecuritySettings(password = null)
        authSession = createAuthSession()
        runCurrent()
        assertEquals(AuthState.NoAuthRequired, authSession.authState.value)

        fakeUseCase.flow.value = SecuritySettings(password = "1234")
        runCurrent()

        assertTrue(authSession.authRequired.value)
        assertEquals(AuthState.AuthRequired(authed = true), authSession.authState.value)
    }

    @Test
    fun `password removed during session - becomes unlocked`() = testScope.runTest {
        fakeUseCase.flow.value = SecuritySettings(password = "1234")
        authSession = createAuthSession()
        runCurrent()
        assertEquals(AuthState.AuthRequired(authed = false), authSession.authState.value)

        fakeUseCase.flow.value = SecuritySettings(password = null)
        runCurrent()

        assertFalse(authSession.authRequired.value)
        assertEquals(AuthState.NoAuthRequired, authSession.authState.value)
    }

    private class FakeManageSecuritySettingsUseCase : ManageSecuritySettingsUseCase {
        val flow = MutableStateFlow(SecuritySettings())
        override val settings: Flow<SecuritySettings> = flow

        override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
            flow.update(action)
        }
    }
}
