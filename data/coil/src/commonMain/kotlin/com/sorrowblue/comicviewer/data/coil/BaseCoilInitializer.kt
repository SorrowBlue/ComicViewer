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
import dev.zacsweers.metro.HasMemberInjections
import dev.zacsweers.metro.Inject
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.logcat

@HasMemberInjections
open class BaseCoilInitializer {

    fun initialize() {
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
    lateinit var factory: Lazy<Map<KClass<*>, Fetcher.Factory<*>>>

    @Inject
    lateinit var keyer: Lazy<Map<KClass<*>, Keyer<*>>>

    @Suppress("UNCHECKED_CAST")
    private val componentRegistry
        get() = ComponentRegistry.Builder()
            .apply {
                factory.value.forEach {
                    add(
                        it.value as Fetcher.Factory<Any>,
                        it.key as KClass<Any>,
                    )
                }
                keyer.value.forEach { add(it.value as Keyer<Any>, it.key as KClass<Any>) }
            }
            .build()

    open fun ImageLoader.Builder.setup() {}
}
