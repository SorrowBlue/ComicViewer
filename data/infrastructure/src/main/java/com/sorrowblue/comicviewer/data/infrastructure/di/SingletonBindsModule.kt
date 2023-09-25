package com.sorrowblue.comicviewer.data.infrastructure.di

import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.BookshelfRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.FavoriteFileRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.FavoriteRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.FileRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.ReadLaterRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.SettingsCommonRepositoryImpl
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.service.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
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
