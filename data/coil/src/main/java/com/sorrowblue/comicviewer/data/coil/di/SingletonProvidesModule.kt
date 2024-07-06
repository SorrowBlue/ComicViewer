package com.sorrowblue.comicviewer.data.coil.di

import android.content.Context
import coil3.disk.DiskCache
import coil3.disk.directory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @Singleton
    @ThumbnailDiskCache
    @Provides
    fun provideThumbnailDiskCache(@ApplicationContext context: Context): DiskCache {
        return context.newDiskCache("thumbnail_cache")
    }

    private fun Context.newDiskCache(folder: String) =
        DiskCache.Builder().directory(cacheDir.resolve(folder).apply { mkdirs() }).build()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ThumbnailDiskCache
