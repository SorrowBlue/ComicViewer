package com.sorrowblue.comicviewer.data.coil.di

import coil3.ComponentRegistry
import coil3.fetch.Fetcher
import coil3.key.Keyer
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

typealias FactoryKeyer = Pair<Fetcher.Factory<*>, Keyer<*>>
typealias FactoryKeyerMap = Map.Entry<KClass<*>, FactoryKeyer>

@Suppress("UNCHECKED_CAST")
val FactoryKeyerMap.type get() = key as KClass<Any>

@Suppress("UNCHECKED_CAST")
val FactoryKeyerMap.factory get() = value.first as Fetcher.Factory<Any>

@Suppress("UNCHECKED_CAST")
val FactoryKeyerMap.keyer get() = value.second as Keyer<Any>

internal annotation class CoilScope

@GraphExtension(CoilScope::class)
interface CoilContext {

    val componentRegistry: ComponentRegistry

    @Provides
    private fun provideComponentRegistry(map: Map<KClass<*>, FactoryKeyer>): ComponentRegistry =
        ComponentRegistry.Builder()
            .apply {
                map.forEach {
                    add(it.factory, it.type)
                    add(it.keyer, it.type)
                }
            }
            .build()

    @Provides
    @ClassKey(FolderThumbnail::class)
    @IntoMap
    private fun provideFolderThumbnailFetcher(
        factory: Fetcher.Factory<FolderThumbnail>,
        keyer: Keyer<FolderThumbnail>,
    ): FactoryKeyer = factory to keyer

    @Provides
    @ClassKey(BookThumbnail::class)
    @IntoMap
    private fun provideBookThumbnailFetcher(
        factory: Fetcher.Factory<BookThumbnail>,
        keyer: Keyer<BookThumbnail>,
    ): FactoryKeyer = factory to keyer

    @Provides
    @ClassKey(BookPageImage::class)
    @IntoMap
    private fun provideBookPageImageFetcher(
        factory: Fetcher.Factory<BookPageImage>,
        keyer: Keyer<BookPageImage>,
    ): FactoryKeyer = factory to keyer

    @Provides
    @ClassKey(Collection::class)
    @IntoMap
    private fun provideCollectionFetcher(
        factory: Fetcher.Factory<Collection>,
        keyer: Keyer<Collection>,
    ): FactoryKeyer = factory to keyer

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createCoilContext(): CoilContext
    }
}
