/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.appGraph
import com.sorrowblue.comicviewer.framework.startup.Initializer
import com.sorrowblue.comicviewer.framework.test.SmallTest
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

@SmallTest
class JvmWindowTest {

    private class FakeViewModel : ViewModel() {
        var isCleared = false
            private set

        override fun onCleared() {
            super.onCleared()
            isCleared = true
        }
    }

    private class FakeWindow(viewModelFactory: ViewModelProvider.Factory) :
        JvmWindow(viewModelFactory) {
        val viewModel by viewModels<FakeViewModel>()

        @Composable
        context(scope: FrameWindowScope, context: PlatformContext)
        override fun WindowContent() = Unit
    }

    @Test
    fun `viewModels retrieves ViewModel outside Composable and close clears ViewModelStore`() {
        var initializerCalled = false
        val fakeInitializer = object : Initializer<Unit> {
            override fun create() {
                initializerCalled = true
            }

            override fun dependencies(): List<KClass<out Initializer<*>>> = emptyList()
        }
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                FakeViewModel() as T
        }
        val testApp = object : JvmApplication() {
            override val appGraph: AppGraph
                get() = throw UnsupportedOperationException()

            override val windowProviders: MetroWindowProviders =
                object : MetroWindowProviders {
                    override val windowProviders: Map<KClass<out JvmWindow>, () -> JvmWindow> =
                        mapOf(FakeWindow::class to { FakeWindow(factory) })
                }

            override fun onCreate() {
                Initializer.initialize(listOf(fakeInitializer))
            }
        }

        testApp.onCreate()
        assertTrue(initializerCalled)

        var closeCalled = false
        val window = testApp.instantiateWindow(FakeWindow::class)
        window.bindClose {
            closeCalled = true
        }
        assertEquals(Lifecycle.State.INITIALIZED, window.lifecycle.currentState)

        window.start()
        assertEquals(Lifecycle.State.RESUMED, window.lifecycle.currentState)

        val vm1 = window.viewModel
        val vm2 = window.viewModel
        assertSame(vm1, vm2)
        assertEquals(false, vm1.isCleared)

        window.close()
        assertTrue(closeCalled)
        assertEquals(Lifecycle.State.DESTROYED, window.lifecycle.currentState)
        assertTrue(vm1.isCleared)
    }

    @Test
    fun `TestApplication instantiates MainWindow via Metro multibinding`() {
        setupTest()
        val testApplication = TestApplication().apply { onCreate() }
        try {
            val window = testApplication.instantiateWindow(MainWindow::class)
            assertIs<MainWindow>(window)
            assertSame(testApplication.appGraph, testApplication.appGraph())
            window.dispose()
        } finally {
            tearDownTest(testApplication.appGraph)
        }
    }
}
