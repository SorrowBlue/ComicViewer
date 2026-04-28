package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class PreAppScreenStateTest {

    private lateinit var testScope: TestScope
    private lateinit var fakeManageSecuritySettingsUseCase: FakeManageSecuritySettingsUseCase
    private lateinit var fakeLoadSettingsUseCase: FakeLoadSettingsUseCase
    private lateinit var state: PreAppScreenStateImpl

    @BeforeTest
    fun setup() {
        testScope = TestScope()
        fakeManageSecuritySettingsUseCase = FakeManageSecuritySettingsUseCase()
        fakeLoadSettingsUseCase = FakeLoadSettingsUseCase()
        state = PreAppScreenStateImpl(
            scope = testScope.backgroundScope,
            manageSecuritySettingsUseCase = fakeManageSecuritySettingsUseCase,
            loadSettingsUseCase = fakeLoadSettingsUseCase,
        )
    }

    @Test
    fun `initial state - unknown auth status`() = testScope.runTest {
        assertEquals(AuthStatus.Unknown, state.authStatus)
    }

    @Test
    fun `tutorial required - updates when settings change`() = testScope.runTest {
        fakeLoadSettingsUseCase.emit(Settings(doneTutorial = false))
        runCurrent()
        assertTrue(state.tutorialRequired)

        fakeLoadSettingsUseCase.emit(Settings(doneTutorial = true))
        runCurrent()
        assertFalse(state.tutorialRequired)
    }

    @Test
    fun `auth status - becomes NoAuthRequired when no password`() = testScope.runTest {
        fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = null))
        runCurrent()
        assertEquals(AuthStatus.NoAuthRequired, state.authStatus)
    }

    @Test
    fun `auth status - becomes AuthRequired when password exists`() = testScope.runTest {
        fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = "password"))
        runCurrent()
        assertIs<AuthStatus.AuthRequired>(state.authStatus)
        assertFalse((state.authStatus as AuthStatus.AuthRequired).authed)
    }

    @Test
    fun `auth status - transitions from NoAuthRequired to AuthRequired as authed`() =
        testScope.runTest {
            fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = null))
            runCurrent()
            assertEquals(AuthStatus.NoAuthRequired, state.authStatus)

            fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = "password"))
            runCurrent()
            assertEquals(AuthStatus.AuthRequired(authed = true), state.authStatus)
        }

    @Test
    fun `onAuthComplete - updates authStatus to authed`() = testScope.runTest {
        fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = "password"))
        runCurrent()

        state.onAuthComplete()
        assertEquals(AuthStatus.AuthRequired(authed = true), state.authStatus)
    }

    @Test
    fun `onAuthComplete - disable password`() = testScope.runTest {
        fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = "password"))
        runCurrent()

        state.onAuthComplete()
        assertEquals(AuthStatus.AuthRequired(authed = true), state.authStatus)

        fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = null))
        runCurrent()
        assertEquals(AuthStatus.NoAuthRequired, state.authStatus)
    }

    @Test
    fun `onAuthComplete - changes password`() = testScope.runTest {
        fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = "password"))
        runCurrent()

        state.onAuthComplete()
        assertEquals(AuthStatus.AuthRequired(authed = true), state.authStatus)

        fakeManageSecuritySettingsUseCase.emit(SecuritySettings(password = "password2"))
        runCurrent()
        assertEquals(AuthStatus.AuthRequired(authed = true), state.authStatus)
    }

    @Test
    fun `onTutorialComplete - updates doneTutorial in settings`() = testScope.runTest {
        fakeLoadSettingsUseCase.emit(Settings(doneTutorial = false))
        runCurrent()
        assertTrue(state.tutorialRequired)

        state.onTutorialComplete()
        runCurrent()
        assertTrue(fakeLoadSettingsUseCase.currentValue.doneTutorial)
    }

    @Test
    fun `onPause - locks when lockOnBackground is true`() = testScope.runTest {
        fakeManageSecuritySettingsUseCase.emit(
            SecuritySettings(password = "password", lockOnBackground = true),
        )
        runCurrent()
        state.onAuthComplete()
        assertEquals(AuthStatus.AuthRequired(authed = true), state.authStatus)

        state.onPause()
        assertEquals(AuthStatus.AuthRequired(authed = false), state.authStatus)
    }

    @Test
    fun `onPause - does not lock when lockOnBackground is false`() = testScope.runTest {
        fakeManageSecuritySettingsUseCase.emit(
            SecuritySettings(password = "password", lockOnBackground = false),
        )
        runCurrent()
        state.onAuthComplete()
        assertEquals(AuthStatus.AuthRequired(authed = true), state.authStatus)

        state.onPause()
        assertEquals(AuthStatus.AuthRequired(authed = true), state.authStatus)
    }

    private class FakeManageSecuritySettingsUseCase : ManageSecuritySettingsUseCase {
        private val _settings = MutableStateFlow(SecuritySettings())
        override val settings = _settings
        override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
            _settings.value = action(_settings.value)
        }
        fun emit(value: SecuritySettings) {
            _settings.value = value
        }
    }

    private class FakeLoadSettingsUseCase : LoadSettingsUseCase {
        private val _settings = MutableStateFlow(Settings())
        override val settings = _settings
        val currentValue get() = _settings.value
        override suspend fun edit(action: (Settings) -> Settings) {
            _settings.value = action(_settings.value)
        }
        fun emit(value: Settings) {
            _settings.value = value
        }
    }
}
