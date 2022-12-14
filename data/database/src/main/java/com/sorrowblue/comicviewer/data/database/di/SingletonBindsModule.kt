package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.impl.FileModelLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.ServerLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.ServerLocalDataSource
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
    abstract fun bindServerLocalDataSource(dataSource: ServerLocalDataSourceImpl): ServerLocalDataSource

    @Singleton
    @Binds
    abstract fun bindFileModelLocalDataSource(dataSource: FileModelLocalDataSourceImpl): FileModelLocalDataSource
}
