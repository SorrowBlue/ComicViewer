package com.sorrowblue.comicviewer.data.coil.di

import coil3.disk.DiskCache
import coil3.fetch.Fetcher
import com.sorrowblue.comicviewer.data.coil.BookPageImageFetcher
import com.sorrowblue.comicviewer.data.coil.BookThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.CollectionThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.FolderThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.book.BookThumbnailFetcherFactory
import com.sorrowblue.comicviewer.data.coil.collection.CollectionThumbnailFetcherFactory
import com.sorrowblue.comicviewer.data.coil.folder.FolderThumbnailFetcherFactory
import com.sorrowblue.comicviewer.data.coil.impl.ImageCacheDataSourceImpl
import com.sorrowblue.comicviewer.data.coil.impl.ThumbnailDataSourceImpl
import com.sorrowblue.comicviewer.data.coil.page.BookPageImageFetcherFactory
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ThumbnailDataSource
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Scope

@Scope
annotation class CoilScope

@ContributesTo(DataScope::class)
interface DataCoilProviders {

    @Provides
    private fun provideDiskCache(coilDiskCache: CoilDiskCache): DiskCache {
        return DiskCache.Builder().directory(coilDiskCache.resolve("image_cache")).build()
    }

    @Provides
    private fun provideCoilDiskCache(context: PlatformContext): CoilDiskCache = CoilDiskCache(context)

    @Binds
    private fun ImageCacheDataSourceImpl.bind(): ImageCacheDataSource = this

    @Binds
    private fun ThumbnailDataSourceImpl.bind(): ThumbnailDataSource = this

    @Binds
    @BookThumbnailFetcher
    private fun BookThumbnailFetcherFactory.bind(): Fetcher.Factory<BookThumbnail> = this

    @Binds
    @BookPageImageFetcher
    private fun BookPageImageFetcherFactory.bind(): Fetcher.Factory<BookPageImage> = this

    @Binds
    @FolderThumbnailFetcher
    private fun FolderThumbnailFetcherFactory.bind(): Fetcher.Factory<FolderThumbnail> = this

    @Binds
    @CollectionThumbnailFetcher
    private fun CollectionThumbnailFetcherFactory.bind(): Fetcher.Factory<Collection> = this
}
