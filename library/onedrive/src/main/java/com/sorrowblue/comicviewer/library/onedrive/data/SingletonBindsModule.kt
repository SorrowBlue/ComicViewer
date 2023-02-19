package com.sorrowblue.comicviewer.library.onedrive.data

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
    abstract fun bindOneDriveApiRepository(repository: OneDriveApiRepositoryImpl): OneDriveApiRepository
}
