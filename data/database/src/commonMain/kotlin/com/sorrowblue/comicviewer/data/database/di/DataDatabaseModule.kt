package com.sorrowblue.comicviewer.data.database.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@ComponentScan("com.sorrowblue.comicviewer.data.database")
class DataDatabaseModule {

    @Singleton
    internal fun provideBookshelfDao(database: ComicViewerDatabase) = database.bookshelfDao()

    @Singleton
    internal fun provideFileDao(database: ComicViewerDatabase) = database.fileDao()

    @Singleton
    internal fun provideFavoriteDao(database: ComicViewerDatabase) = database.favoriteDao()

    @Singleton
    internal fun provideFavoriteFileDao(database: ComicViewerDatabase) = database.favoriteFileDao()

    @Singleton
    internal fun provideReadLaterFileDao(database: ComicViewerDatabase) =
        database.readLaterFileDao()

    @Singleton
    internal fun getRoomDatabase(helper: DatabaseHelper): ComicViewerDatabase {
        return helper.getDatabaseBuilder()
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
