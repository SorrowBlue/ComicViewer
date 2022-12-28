package com.sorrowblue.comicviewer.data.paging

import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
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
    abstract fun bindFileModelRemoteMediatorFactory(dataSource: FileModelRemoteMediatorImpl.Factory): FileModelRemoteMediator.Factory

}
