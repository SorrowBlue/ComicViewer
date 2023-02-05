package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.impl.FavoriteBookLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FavoriteLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FileModelLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.ReadLaterLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.ServerLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.datasource.FavoriteBookLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.ReadLaterLocalDataSource
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
    abstract fun bindReadLaterLocalDataSource(dataSource: ReadLaterLocalDataSourceImpl): ReadLaterLocalDataSource

    @Singleton
    @Binds
    abstract fun bindFileModelLocalDataSource(dataSource: FileModelLocalDataSourceImpl): FileModelLocalDataSource

    @Singleton
    @Binds
    abstract fun bindFavoriteLocalDataSource(dataSource: FavoriteLocalDataSourceImpl): FavoriteLocalDataSource

    @Singleton
    @Binds
    abstract fun bindFavoriteBookLocalDataSource(dataSource: FavoriteBookLocalDataSourceImpl): FavoriteBookLocalDataSource
}
