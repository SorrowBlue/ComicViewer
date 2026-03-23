package com.sorrowblue.comicviewer.data.coil.di

import coil3.ComponentRegistry
import coil3.fetch.Fetcher
import coil3.key.Keyer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

internal annotation class CoilScope

@GraphExtension(CoilScope::class)
interface CoilContext {

    val componentRegistry: ComponentRegistry

    @Suppress("UNCHECKED_CAST")
    @Provides
    private fun provideComponentRegistry(
        factory: Map<KClass<*>, Fetcher.Factory<*>>,
        keyer: Map<KClass<*>, Keyer<*>>,
    ): ComponentRegistry = ComponentRegistry.Builder()
        .apply {
            factory.forEach {
                add(it.value as Fetcher.Factory<Any>, it.key as KClass<Any>)
            }
            keyer.forEach {
                add(it.value as Keyer<Any>, it.key as KClass<Any>)
            }
        }
        .build()

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createCoilContext(): CoilContext
    }
}
