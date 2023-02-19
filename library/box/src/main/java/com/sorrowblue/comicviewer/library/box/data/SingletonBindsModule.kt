package com.sorrowblue.comicviewer.library.box.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Binds
    abstract fun bindBoxApiRepository(repository: BoxApiRepositoryImpl): BoxApiRepository
}
