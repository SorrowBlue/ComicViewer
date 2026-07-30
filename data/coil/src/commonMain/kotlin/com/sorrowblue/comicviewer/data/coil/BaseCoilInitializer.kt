/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil

import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.fetch.Fetcher
import coil3.key.Keyer
import coil3.request.crossfade
import coil3.size.Precision
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.appGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.HasMemberInjections
import dev.zacsweers.metro.Inject
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.logcat

@HasMemberInjections
open class BaseCoilInitializer {

    fun initialize(platformContext: PlatformContext) {
        platformContext.appGraph<CoilInitializerInjector>().inject(this)
        SingletonImageLoader.setSafe { context ->
            ImageLoader(context)
                .newBuilder()
                .components(componentRegistry)
                .crossfade(true)
                .precision(Precision.INEXACT)
                .apply { setup() }
                .build()
        }
        logcat(LogPriority.INFO) { "Initialized coil." }
    }

    @Inject
    lateinit var factory: Map<KClass<*>, Fetcher.Factory<*>>

    @Inject
    lateinit var keyer: Map<KClass<*>, Keyer<*>>

    @Suppress("UNCHECKED_CAST")
    private val componentRegistry
        get() = ComponentRegistry.Builder()
            .apply {
                factory.forEach { add(it.value as Fetcher.Factory<Any>, it.key as KClass<Any>) }
                keyer.forEach { add(it.value as Keyer<Any>, it.key as KClass<Any>) }
            }
            .build()

    open fun ImageLoader.Builder.setup() {}
}

@ContributesTo(AppScope::class)
interface CoilInitializerInjector {
    fun inject(target: BaseCoilInitializer)
}
