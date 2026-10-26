/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.authentication.nav.ScreenType
import com.sorrowblue.comicviewer.framework.test.SmallTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@SmallTest
@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationScreenStateTest {

    private lateinit var testScope: TestScope
    private lateinit var fakeUseCase: FakeManageSecuritySettingsUseCase
    private lateinit var fakeBiometricManager: FakeBiometricManager
    private lateinit var fakeLifecycleOwner: FakeLifecycleOwner

    @BeforeTest
    fun setup() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        testScope = TestScope(dispatcher)
        fakeUseCase = FakeManageSecuritySettingsUseCase()
        fakeBiometricManager = FakeBiometricManager()
        fakeLifecycleOwner = FakeLifecycleOwner()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `biometric authentication waits for RESUMED when initialized in background`() =
        testScope.runTest {
            fakeUseCase.flow.value = SecuritySettings(
                password = "1234",
                useBiometrics = true,
                lockOnBackground = true,
            )
            fakeLifecycleOwner.currentState = Lifecycle.State.STARTED

            val state = AuthenticationScreenStateImpl(
                screenType = ScreenType.Authenticate,
                scope = backgroundScope,
                snackbarHostState = SnackbarHostState(),
                biometricManager = fakeBiometricManager,
                viewModel = AuthenticationViewModel(fakeUseCase),
                pinInputFlowStateHolder = PinInputFlowStateHolder(),
                lifecycleOwner = fakeLifecycleOwner,
            )
            runCurrent()

            // Should not trigger biometric prompt while in background (STARTED / paused)
            assertEquals(0, fakeBiometricManager.authenticateCallCount)

            var completed = false
            backgroundScope.launch {
                if (state.events.first() == AuthenticationScreenEvent.Complete) {
                    completed = true
                }
            }
            runCurrent()

            // Transition to RESUMED (returning to foreground)
            fakeLifecycleOwner.currentState = Lifecycle.State.RESUMED
            runCurrent()

            assertEquals(1, fakeBiometricManager.authenticateCallCount)
            assertEquals(true, completed)
        }

    private class FakeBiometricManager : BiometricManager {
        var authenticateCallCount = 0
        var result: AuthenticationResult = AuthenticationResult.Success

        override suspend fun authenticate(): AuthenticationResult {
            authenticateCallCount++
            return result
        }
    }

    private class FakeLifecycleOwner : LifecycleOwner {
        private val registry = LifecycleRegistry.createUnsafe(this)

        var currentState: Lifecycle.State
            get() = registry.currentState
            set(value) {
                registry.currentState = value
            }

        override val lifecycle: Lifecycle
            get() = registry
    }

    private class FakeManageSecuritySettingsUseCase : ManageSecuritySettingsUseCase {
        val flow = MutableStateFlow(SecuritySettings())
        override val settings: Flow<SecuritySettings> = flow

        override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
            flow.update(action)
        }
    }
}
