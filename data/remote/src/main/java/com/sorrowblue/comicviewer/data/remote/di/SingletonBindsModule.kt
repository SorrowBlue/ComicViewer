package com.sorrowblue.comicviewer.data.remote.di

import android.content.Context
import coil.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.RemoteFileThumbnailFetcherFactory
import com.sorrowblue.comicviewer.data.coil.SmbFetcherFactory
import com.sorrowblue.comicviewer.data.datasource.FileModelRemoteDataSource
import com.sorrowblue.comicviewer.data.remote.RemoteFileThumbnailFetcher
import com.sorrowblue.comicviewer.data.remote.SmbFetcher
import com.sorrowblue.comicviewer.data.remote.impl.FileModelRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Binds
    abstract fun bindFileModelRemoteDataSourceFactory(factory: FileModelRemoteDataSourceImpl.Factory): FileModelRemoteDataSource.Factory

    @Binds
    abstract fun bindRemoteFileThumbnailFetcherFactoryFactory(factory: RemoteFileThumbnailFetcher.Factory.Factory): RemoteFileThumbnailFetcherFactory.Factory

    @Binds
    abstract fun bindSmbFetcherFactory(factory: SmbFetcher.Factory): SmbFetcherFactory
}


@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @Singleton
    @ThumbnailDiskCache
    @Provides
    fun provideThumbnailDiskCache(@ApplicationContext context: Context): DiskCache? {
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
annotation class ThumbnailDiskCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PageDiskCache
