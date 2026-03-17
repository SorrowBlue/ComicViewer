package com.sorrowblue.comicviewer.data.coil.di

import coil3.disk.DiskCache
import coil3.fetch.Fetcher
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.book.BookThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.collection.CollectionThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.folder.FolderThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.impl.ImageCacheDataSourceImpl
import com.sorrowblue.comicviewer.data.coil.impl.ThumbnailDataSourceImpl
import com.sorrowblue.comicviewer.data.coil.page.BookPageImageFetcher
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
    private fun provideDiskCache(coilDiskCache: CoilDiskCache): DiskCache =
        DiskCache.Builder().directory(coilDiskCache.resolve("image_cache")).build()

    @Provides
    private fun provideCoilDiskCache(context: PlatformContext): CoilDiskCache =
        CoilDiskCache(context)

    @Binds
    private val ImageCacheDataSourceImpl.bind: ImageCacheDataSource get() = this

    @Binds
    private val ThumbnailDataSourceImpl.bind: ThumbnailDataSource get() = this

    @Binds
    private val BookThumbnailFetcher.Factory.bind: Fetcher.Factory<BookThumbnail> get() = this

    @Binds
    private val BookPageImageFetcher.Factory.bind: Fetcher.Factory<BookPageImage> get() = this

    @Binds
    private val FolderThumbnailFetcher.Factory.bind: Fetcher.Factory<FolderThumbnail> get() = this

    @Binds
    private val CollectionThumbnailFetcher.Factory.bind: Fetcher.Factory<Collection> get() = this
}
