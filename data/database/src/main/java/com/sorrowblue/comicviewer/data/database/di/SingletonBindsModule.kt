package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.impl.BookshelfLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FavoriteFileLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FavoriteLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FileModelLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.ReadLaterFileModelLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.infrastructure.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.ReadLaterFileModelLocalDataSource
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
    fun bindBookshelfLocalDataSource(dataSource: BookshelfLocalDataSourceImpl): BookshelfLocalDataSource

    @Singleton
    @Binds
    fun bindReadLaterFileModelLocalDataSource(
        dataSource: ReadLaterFileModelLocalDataSourceImpl,
    ): ReadLaterFileModelLocalDataSource

    @Singleton
    @Binds
    fun bindFileModelLocalDataSource(dataSource: FileModelLocalDataSourceImpl): FileModelLocalDataSource

    @Singleton
    @Binds
    fun bindFavoriteLocalDataSource(dataSource: FavoriteLocalDataSourceImpl): FavoriteLocalDataSource

    @Singleton
    @Binds
    fun bindFavoriteBookLocalDataSource(
        dataSource: FavoriteFileLocalDataSourceImpl,
    ): FavoriteFileLocalDataSource
}
