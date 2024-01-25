package com.sorrowblue.comicviewer.data.infrastructure.di

import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.BookshelfRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.FavoriteFileRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.FavoriteRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.FileRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.ReadLaterRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.SettingsCommonRepositoryImpl
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.SplitInstallRepositoryImpl
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.service.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.service.repository.SplitInstallRepository
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
    fun bindFileRepository(repository: FileRepositoryImpl): FileRepository

    @Singleton
    @Binds
    fun bindReadLaterRepository(repository: ReadLaterRepositoryImpl): ReadLaterRepository

    @Singleton
    @Binds
    fun bindSettingsCommonRepository(repository: SettingsCommonRepositoryImpl): SettingsCommonRepository

    @Singleton
    @Binds
    fun bindBookshelfRepository(repository: BookshelfRepositoryImpl): BookshelfRepository

    @Singleton
    @Binds
    fun bindFavoriteRepository(factory: FavoriteRepositoryImpl): FavoriteRepository

    @Singleton
    @Binds
    fun bindFavoriteBookRepository(factory: FavoriteFileRepositoryImpl): FavoriteFileRepository

    @Singleton
    @Binds
    fun bindSplitInstallRepository(repository: SplitInstallRepositoryImpl): SplitInstallRepository
}
