package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.app.wrapper.PreAppScreenStateImpl
import com.sorrowblue.comicviewer.app.wrapper.PreAppUiState
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("VisibleForTests")
class PreAppScreenStateTest {

    private lateinit var testScope: TestScope
    private lateinit var state: PreAppScreenStateImpl

    val tutorialRequiredFlow = MutableSharedFlow<Boolean>(replay = 1)
    val authRequiredFlow = MutableSharedFlow<Boolean>(replay = 1)
    val lockOnBackgroundFlow = MutableStateFlow(false)

    @BeforeTest
    fun setup() {
        testScope = TestScope()

        state = PreAppScreenStateImpl(
            scope = testScope.backgroundScope,
            tutorialRequired = tutorialRequiredFlow,
            authRequired = authRequiredFlow,
            lockOnBackground = lockOnBackgroundFlow,
            tutorialComplete = {
                testScope.launch {
                    tutorialRequiredFlow.emit(false)
                }
            },
        )
    }

    @Test
    fun `initial state - is Loading`() = testScope.runTest {
        assertEquals(PreAppUiState.Loading, state.uiState)
    }

    @Test
    fun `tutorial required - updates when settings change`() = testScope.runTest {
        tutorialRequiredFlow.emit(true)
        authRequiredFlow.emit(false)
        runCurrent()
        assertEquals(PreAppUiState.TutorialRequired, state.uiState)

        tutorialRequiredFlow.emit(false)
        runCurrent()
        assertEquals(PreAppUiState.NoAuthRequired, state.uiState)
    }

    @Test
    fun `auth status - becomes NoAuthRequired when no password`() = testScope.runTest {
        tutorialRequiredFlow.emit(false)
        authRequiredFlow.emit(false)
        runCurrent()
        assertEquals(PreAppUiState.NoAuthRequired, state.uiState)
    }

    @Test
    fun `auth status - becomes AuthRequired when password exists`() = testScope.runTest {
        tutorialRequiredFlow.emit(false)
        authRequiredFlow.emit(true)
        runCurrent()
        assertEquals(PreAppUiState.AuthRequired(authed = false), state.uiState)
    }

    @Test
    fun `auth status - transitions from NoAuthRequired to AuthRequired as authed`() =
        testScope.runTest {
            tutorialRequiredFlow.emit(false)
            authRequiredFlow.emit(false)
            runCurrent()
            assertEquals(PreAppUiState.NoAuthRequired, state.uiState)

            authRequiredFlow.emit(true)
            runCurrent()
            assertEquals(PreAppUiState.AuthRequired(authed = true), state.uiState)
        }

    @Test
    fun `onAuthComplete - updates authStatus to authed`() = testScope.runTest {
        tutorialRequiredFlow.emit(false)
        authRequiredFlow.emit(true)
        runCurrent()

        state.onAuthComplete()
        assertEquals(PreAppUiState.AuthRequired(authed = true), state.uiState)
    }

    @Test
    fun `onAuthComplete - disable password`() = testScope.runTest {
        tutorialRequiredFlow.emit(false)
        authRequiredFlow.emit(true)
        runCurrent()

        state.onAuthComplete()
        assertEquals(PreAppUiState.AuthRequired(authed = true), state.uiState)

        authRequiredFlow.emit(false)
        runCurrent()
        assertEquals(PreAppUiState.NoAuthRequired, state.uiState)
    }

    @Test
    fun `onAuthComplete - changes password`() = testScope.runTest {
        tutorialRequiredFlow.emit(false)
        authRequiredFlow.emit(true)
        runCurrent()

        state.onAuthComplete()
        assertEquals(PreAppUiState.AuthRequired(authed = true), state.uiState)

        authRequiredFlow.emit(true)
        runCurrent()
        assertEquals(PreAppUiState.AuthRequired(authed = true), state.uiState)
    }

    @Test
    fun `onTutorialComplete - updates doneTutorial in settings`() = testScope.runTest {
        tutorialRequiredFlow.emit(true)
        authRequiredFlow.emit(false)
        runCurrent()
        assertEquals(PreAppUiState.TutorialRequired, state.uiState)

        state.onTutorialComplete()
        runCurrent()
        assertTrue(!tutorialRequiredFlow.replayCache.first())
    }

    @Test
    fun `onPause - locks when lockOnBackground is true`() = testScope.runTest {
        tutorialRequiredFlow.emit(false)
        authRequiredFlow.emit(true)
        lockOnBackgroundFlow.value = true
        runCurrent()
        state.onAuthComplete()
        assertEquals(PreAppUiState.AuthRequired(authed = true), state.uiState)

        state.onPause()
        assertEquals(PreAppUiState.AuthRequired(authed = false), state.uiState)
    }

    @Test
    fun `onPause - does not lock when lockOnBackground is false`() = testScope.runTest {
        tutorialRequiredFlow.emit(false)
        authRequiredFlow.emit(true)
        lockOnBackgroundFlow.value = false
        runCurrent()
        state.onAuthComplete()
        assertEquals(PreAppUiState.AuthRequired(authed = true), state.uiState)

        state.onPause()
        assertEquals(PreAppUiState.AuthRequired(authed = true), state.uiState)
    }
}
