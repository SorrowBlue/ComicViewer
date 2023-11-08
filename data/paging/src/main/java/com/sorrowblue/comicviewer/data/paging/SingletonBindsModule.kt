package com.sorrowblue.comicviewer.data.paging

import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
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
    fun bindFileModelRemoteMediatorFactory(
        dataSource: FileModelRemoteMediatorImpl.Factory,
    ): FileModelRemoteMediator.Factory
}
