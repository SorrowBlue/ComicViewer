/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ApplicationScope
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.DesktopContext
import com.sorrowblue.comicviewer.framework.startup.Initializer
import dev.zacsweers.metro.MapKey
import dev.zacsweers.metro.Multibinds
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.logcat

/**
 * A [MapKey] annotation for binding [JvmWindow] into a multibinding map.
 *
 * @property value The [KClass] of the [JvmWindow] being bound.
 */
@MapKey(implicitClassKey = true)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class WindowKey(val value: KClass<out JvmWindow> = Nothing::class)

/**
 * Provides multibindings for [JvmWindow] instances.
 */
interface MetroWindowProviders {
    /**
     * Map of [JvmWindow] providers keyed by their [KClass].
     */
    @Multibinds(allowEmpty = true)
    val windowProviders: Map<KClass<out JvmWindow>, () -> JvmWindow>
}

/**
 * Base class for maintaining global application state and DI graph on JVM Desktop.
 */
abstract class JvmApplication :
    DesktopContext(),
    AppGraphProvider<AppGraph> {

    /**
     * The [MetroWindowProviders] instance used to instantiate windows.
     */
    open val windowProviders: MetroWindowProviders
        get() = appGraph

    /**
     * Initializes application-level components via [Initializer].
     */
    open fun onCreate() {
        Initializer.initialize(appGraph.initializer.toList())
        logcat(LogPriority.INFO) { "onCreate" }
    }

    /**
     * Instantiates a [JvmWindow] of the given [kClass] using [windowProviders].
     *
     * @param kClass The [KClass] of the window to instantiate.
     * @return The instantiated [JvmWindow].
     */
    fun <T : JvmWindow> instantiateWindow(kClass: KClass<T>): T {
        val provider = windowProviders.windowProviders[kClass]
            ?: error("No window provider found for $kClass")
        @Suppress("UNCHECKED_CAST")
        return provider() as T
    }
}

/**
 * Starts and hosts the specified [JvmWindow] within the Compose Desktop [ApplicationScope].
 *
 * @param T The type of [JvmWindow] to launch.
 */
@Composable
context(scope: ApplicationScope)
inline fun <reified T : JvmWindow> JvmApplication.startWindow() {
    val window = remember(this) {
        instantiateWindow(T::class).apply {
            bindClose(scope::exitApplication)
            start()
        }
    }
    DisposableEffect(window) {
        onDispose {
            window.dispose()
        }
    }
    context(this) {
        window.Content()
    }
}
