/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.sorrowblue.comicviewer.app.window.RecoverableWindow
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import kotlin.reflect.KClass

/**
 * Base class for a JVM Desktop window that manages [Lifecycle], [ViewModelStore],
 * and Compose window content hosting.
 *
 * @param viewModelFactory The [ViewModelProvider.Factory] used to create [ViewModel] instances.
 */
abstract class JvmWindow(
    private val viewModelFactory: ViewModelProvider.Factory =
        ViewModelProvider.NewInstanceFactory.instance,
) : LifecycleOwner,
    ViewModelStoreOwner,
    HasDefaultViewModelProviderFactory {

    private var onCloseCallback: () -> Unit = {}
    private var isDisposed = false

    private val lifecycleRegistry = LifecycleRegistry.createUnsafe(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val viewModelStore: ViewModelStore = ViewModelStore()

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                runCatching {
                    viewModelFactory.create(modelClass, extras)
                }.getOrElse {
                    ViewModelProvider.NewInstanceFactory.instance.create(modelClass, extras)
                }
        }
    }

    override val defaultViewModelCreationExtras: CreationExtras
        get() = MutableCreationExtras().apply {
            set(VIEW_MODEL_STORE_OWNER_KEY, this@JvmWindow)
        }

    /**
     * Window title displayed in the title bar.
     */
    open val title: String
        @Composable get() = "Untitled"

    /**
     * Window icon displayed in the title bar and taskbar.
     */
    open val icon: Painter?
        @Composable get() = null

    /**
     * Binds the callback invoked when [close] is called.
     */
    fun bindClose(onClose: () -> Unit) {
        this.onCloseCallback = onClose
    }

    /**
     * Starts this window's lifecycle, transitioning it to [Lifecycle.State.RESUMED].
     */
    fun start() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    /**
     * Disposes this window's lifecycle and clears its [viewModelStore].
     */
    fun dispose() {
        if (isDisposed) return
        isDisposed = true
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
        viewModelStore.clear()
    }

    /**
     * Closes this window, disposing its lifecycle/ViewModelStore and invoking the close callback.
     */
    fun close() {
        dispose()
        onCloseCallback()
    }

    /**
     * Renders the Composable content inside the window frame.
     */
    @Composable
    context(scope: FrameWindowScope, context: PlatformContext)
    abstract fun WindowContent()

    /**
     * Hosts the [RecoverableWindow] with [LocalLifecycleOwner], [LocalViewModelStoreOwner],
     * and [MetroContent] provided.
     */
    @Composable
    context(scope: ApplicationScope, context: PlatformContext)
    fun Content() {
        CompositionLocalProvider(
            LocalLifecycleOwner provides this,
            LocalViewModelStoreOwner provides this,
        ) {
            MetroContent {
                RecoverableWindow(
                    onCloseRequest = ::close,
                    title = title,
                    icon = icon,
                ) {
                    WindowContent()
                }
            }
        }
    }
}

/**
 * Returns a [Lazy] delegate to access the [JvmWindow]'s [ViewModel].
 */
inline fun <reified VM : ViewModel> JvmWindow.viewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null,
): Lazy<VM> = ViewModelLazy(
    viewModelClass = VM::class,
    storeProducer = { viewModelStore },
    factoryProducer = factoryProducer ?: { defaultViewModelProviderFactory },
    extrasProducer = { extrasProducer?.invoke() ?: defaultViewModelCreationExtras },
)
