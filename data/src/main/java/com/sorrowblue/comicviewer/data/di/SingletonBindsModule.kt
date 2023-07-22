package com.sorrowblue.comicviewer.data.di

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
    abstract fun bindFavoriteRepository(factory: FavoriteRepositoryImpl): FavoriteRepository

    @Singleton
    @Binds
    abstract fun bindFavoriteBookRepository(factory: FavoriteFileRepositoryImpl): FavoriteFileRepository
}
