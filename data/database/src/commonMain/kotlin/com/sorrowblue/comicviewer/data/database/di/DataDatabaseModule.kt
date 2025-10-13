package com.sorrowblue.comicviewer.data.database.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.DatabaseHelper
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import jakarta.inject.Singleton

@Singleton
internal fun provideBookshelfDao(database: ComicViewerDatabase) = database.bookshelfDao()

@Singleton
internal fun provideFileDao(database: ComicViewerDatabase) = database.fileDao()

@Singleton
internal fun provideCollectionDao(database: ComicViewerDatabase) = database.collectionDao()

@Singleton
internal fun provideCollectionFileDao(database: ComicViewerDatabase) = database.collectionFileDao()

@Singleton
internal fun provideReadLaterFileDao(database: ComicViewerDatabase) =
    database.readLaterFileDao()

@Singleton
internal fun getRoomDatabase(
    helper: DatabaseHelper,
    decryptedPasswordConverters: DecryptedPasswordConverters,
): ComicViewerDatabase {
    return helper.getDatabaseBuilder()
        .addMigrations(ComicViewerDatabase.ManualMigration7to8())
        .addTypeConverter(decryptedPasswordConverters)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .build()
}
