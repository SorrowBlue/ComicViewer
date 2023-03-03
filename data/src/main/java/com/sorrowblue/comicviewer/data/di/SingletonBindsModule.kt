package com.sorrowblue.comicviewer.data.di

import coil.fetch.Fetcher
import com.sorrowblue.comicviewer.data.coil.page.BookPageFetcher
import com.sorrowblue.comicviewer.data.coil.FavoriteThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.book.BookThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.folder.FolderThumbnailFetcher
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.reporitory.FavoriteFileRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.FavoriteRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.FileRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.ReadLaterRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.SettingsCommonRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.impl.BookshelfRepositoryImpl
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
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
    abstract fun bindFileRepository(repository: FileRepositoryImpl): FileRepository

    @Singleton
    @Binds
    abstract fun bindReadLaterRepository(repository: ReadLaterRepositoryImpl): ReadLaterRepository

    @Singleton
    @Binds
    abstract fun bindSettingsCommonRepository(repository: SettingsCommonRepositoryImpl): SettingsCommonRepository

    @Singleton
    @Binds
    abstract fun bindBookshelfRepository(repository: BookshelfRepositoryImpl): BookshelfRepository

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
    abstract fun bindFavoriteRepository(factory: FavoriteRepositoryImpl): FavoriteRepository

    @Singleton
    @Binds
    abstract fun bindFavoriteBookRepository(factory: FavoriteFileRepositoryImpl): FavoriteFileRepository
}
