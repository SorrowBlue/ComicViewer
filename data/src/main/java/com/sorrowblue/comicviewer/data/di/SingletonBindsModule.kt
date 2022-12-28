package com.sorrowblue.comicviewer.data.di

import com.sorrowblue.comicviewer.data.reporitory.FileRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.SettingsCommonRepositoryImpl
import com.sorrowblue.comicviewer.data.reporitory.impl.ServerRepositoryImpl
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Singleton
    @Binds
    abstract fun bindFileRepository(repository: FileRepositoryImpl): FileRepository

    @Singleton
    @Binds
    abstract fun bindSettingsCommonRepository(repository: SettingsCommonRepositoryImpl): SettingsCommonRepository

    @Singleton
    @Binds
    abstract fun bindServerRepository(repository: ServerRepositoryImpl): ServerRepository
}
