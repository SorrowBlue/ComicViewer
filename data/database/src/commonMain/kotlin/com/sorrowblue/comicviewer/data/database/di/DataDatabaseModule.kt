package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Singleton

@Singleton
internal fun provideBookshelfDao(database: ComicViewerDatabase) = database.bookshelfDao()

@Singleton
internal fun provideFileDao(database: ComicViewerDatabase) = database.fileDao()

@Singleton
internal fun provideFavoriteDao(database: ComicViewerDatabase) = database.favoriteDao()

@Singleton
internal fun provideCollectionDao(database: ComicViewerDatabase) = database.collectionDao()

@Singleton
internal fun provideCollectionFileDao(database: ComicViewerDatabase) = database.collectionFileDao()

@Singleton
internal fun provideFavoriteFileDao(database: ComicViewerDatabase) = database.favoriteFileDao()

@Singleton
internal fun provideReadLaterFileDao(database: ComicViewerDatabase) =
    database.readLaterFileDao()

@Singleton
internal fun getRoomDatabase(helper: DatabaseHelper): ComicViewerDatabase {
    return helper.getDatabaseBuilder()
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
