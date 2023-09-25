package com.sorrowblue.comicviewer.data.coil

import coil.fetch.Fetcher
import com.sorrowblue.comicviewer.data.coil.book.BookThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.favorite.FavoriteThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.folder.FolderThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.page.BookPageFetcher
import com.sorrowblue.comicviewer.data.infrastructure.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.data.model.BookPageRequestData
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.favorite.FavoriteModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Singleton
    @Binds
    abstract fun bindBookPageFetcherFetcher(factory: BookPageFetcher.Factory): Fetcher.Factory<BookPageRequestData>

    @Singleton
    @Binds
    abstract fun bindBookThumbnailFetcherFetcher(factory: BookThumbnailFetcher.Factory): Fetcher.Factory<FileModel.Book>

    @Singleton
    @Binds
    abstract fun bindFolderThumbnailFetcherFetcher(factory: FolderThumbnailFetcher.Factory): Fetcher.Factory<FileModel.Folder>

    @Singleton
    @Binds
    abstract fun bindFavoriteThumbnailFetcherFetcher(factory: FavoriteThumbnailFetcher.Factory): Fetcher.Factory<FavoriteModel>

    @Singleton
    @Binds
    abstract fun bindImageCacheDataSource(factory: ImageCacheDataSourceImpl): ImageCacheDataSource
}
