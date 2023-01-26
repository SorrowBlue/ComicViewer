package com.sorrowblue.comicviewer.data.di

import android.content.Context
import coil.disk.DiskCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @ThumbnailDiskCache
    @Provides
    fun provideThumbnailDiskCache(@ApplicationContext context: Context): DiskCache {
        return context.newDiskCache("thumbnail_cache")
    }

    @Singleton
    @PageDiskCache
    @Provides
    fun providePageDiskCache(@ApplicationContext context: Context): DiskCache? {
        return context.newDiskCache("page_cache")
    }

    private fun Context.newDiskCache(folder: String) =
        DiskCache.Builder().directory(cacheDir.resolve(folder).apply { mkdirs() }).build()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ThumbnailDiskCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PageDiskCache
