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
    @ImageCacheDiskCache
    @Provides
    fun provideImageCacheDiskCacheDiskCache(@ApplicationContext context: Context): DiskCache {
        return context.newDiskCache("image_cache")
    }

    private fun Context.newDiskCache(folder: String) =
        DiskCache.Builder().directory(cacheDir.resolve(folder).apply { mkdirs() }).build()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class ImageCacheDiskCache
