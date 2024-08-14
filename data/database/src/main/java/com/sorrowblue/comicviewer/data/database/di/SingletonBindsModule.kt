package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.impl.BookshelfLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FavoriteFileLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FavoriteLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FileModelLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.ReadLaterFileLocalDataSourceImpl
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
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
        dataSource: ReadLaterFileLocalDataSourceImpl,
    ): ReadLaterFileLocalDataSource

    @Singleton
    @Binds
    fun bindFileModelLocalDataSource(dataSource: FileModelLocalDataSourceImpl): FileLocalDataSource

    @Singleton
    @Binds
    fun bindFavoriteLocalDataSource(dataSource: FavoriteLocalDataSourceImpl): FavoriteLocalDataSource

    @Singleton
    @Binds
    fun bindFavoriteBookLocalDataSource(dataSource: FavoriteFileLocalDataSourceImpl): FavoriteFileLocalDataSource
}
