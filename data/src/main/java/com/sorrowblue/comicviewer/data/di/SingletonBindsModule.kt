package com.sorrowblue.comicviewer.data.di

import coil.fetch.Fetcher
import com.sorrowblue.comicviewer.data.coil.BookPageFetcher
import com.sorrowblue.comicviewer.data.coil.BookThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.FavoriteThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.FileThumbnailFetcher
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerFileModel
import com.sorrowblue.comicviewer.data.reporitory.FavoriteBookRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.FavoriteRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.FileRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.ReadLaterRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.SettingsCommonRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.impl.ServerRepositoryImpl
import com.sorrowblue.comicviewer.domain.repository.FavoriteBookRepository
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
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
    abstract fun bindServerRepository(repository: ServerRepositoryImpl): ServerRepository

    @Singleton
    @Binds
    abstract fun bindBookPageFetcherFetcher(factory: BookPageFetcher.Factory): Fetcher.Factory<BookPageRequestData>

    @Singleton
    @Binds
    abstract fun bindBookThumbnailFetcherFetcher(factory: BookThumbnailFetcher.Factory): Fetcher.Factory<ServerFileModel>

    @Singleton
    @Binds
    abstract fun bindFileThumbnailFetcherFetcher(factory: FileThumbnailFetcher.Factory): Fetcher.Factory<FileModel>

    @Singleton
    @Binds
    abstract fun bindFavoriteThumbnailFetcherFetcher(factory: FavoriteThumbnailFetcher.Factory): Fetcher.Factory<FavoriteModel>

    @Singleton
    @Binds
    abstract fun bindFavoriteRepository(factory: FavoriteRepositoryImpl): FavoriteRepository

    @Singleton
    @Binds
    abstract fun bindFavoriteBookRepository(factory: FavoriteBookRepositoryImpl): FavoriteBookRepository
}
