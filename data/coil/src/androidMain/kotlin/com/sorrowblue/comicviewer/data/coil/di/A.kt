package com.sorrowblue.comicviewer.data.coil.di

import android.content.Context
import coil3.PlatformContext
import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.impl.ImageCacheDataSourceImplDagger
import com.sorrowblue.comicviewer.data.coil.impl.ThumbnailDataSourceImpl
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ThumbnailDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Singleton
    @Binds
    fun bindImageCacheDataSource(factory: ImageCacheDataSourceImplDagger): ImageCacheDataSource

    @Singleton
    @Binds
    fun bindThumbnailDataSource(factory: ThumbnailDataSourceImpl): ThumbnailDataSource
}

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @Singleton
    @Provides
    fun provideImageCacheDiskCacheDiskCache(coilDiskCache: CoilDiskCache): DiskCache {
        return DiskCache.Builder().directory(coilDiskCache.resolve("image_cache")).build()
    }

    @Singleton
    @Provides
    fun provideCoilDiskCache(@ApplicationContext context: Context) = CoilDiskCache(context)

    @Singleton
    @Provides
    fun providePlatformContext(@ApplicationContext context: Context): PlatformContext = context
}
