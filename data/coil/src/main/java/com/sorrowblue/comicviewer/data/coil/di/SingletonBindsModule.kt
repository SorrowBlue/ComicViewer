package com.sorrowblue.comicviewer.data.coil.di

import coil3.fetch.Fetcher
import com.sorrowblue.comicviewer.data.coil.book.BookThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.favorite.FavoriteThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.folder.FolderThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.impl.ImageCacheDataSourceImpl
import com.sorrowblue.comicviewer.data.coil.impl.ThumbnailDataSourceImpl
import com.sorrowblue.comicviewer.data.coil.page.BookPageFetcher
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ThumbnailDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Singleton
    @Binds
    fun bindBookPageFetcherFetcher(factory: BookPageFetcher.Factory): Fetcher.Factory<BookPageRequest>

    @Singleton
    @Binds
    fun bindBookThumbnailFetcher(factory: BookThumbnailFetcher.Factory): Fetcher.Factory<BookThumbnail>

    @Singleton
    @Binds
    fun bindFolderThumbnailFetcher(factory: FolderThumbnailFetcher.Factory): Fetcher.Factory<FolderThumbnail>

    @Singleton
    @Binds
    fun bindFavoriteThumbnailFetcherFetcher(factory: FavoriteThumbnailFetcher.Factory): Fetcher.Factory<Favorite>

    @Singleton
    @Binds
    fun bindImageCacheDataSource(factory: ImageCacheDataSourceImpl): ImageCacheDataSource

    @Singleton
    @Binds
    fun bindThumbnailDataSource(factory: ThumbnailDataSourceImpl): ThumbnailDataSource
}
