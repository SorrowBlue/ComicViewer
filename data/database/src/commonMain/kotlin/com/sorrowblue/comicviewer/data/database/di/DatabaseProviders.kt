package com.sorrowblue.comicviewer.data.database.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.DatabaseHelper
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import com.sorrowblue.comicviewer.data.database.impl.BookshelfLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.CollectionFileLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.CollectionLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FileLocalDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.FileRemoteDataSourceImpl
import com.sorrowblue.comicviewer.data.database.impl.ReadLaterFileLocalDataSourceImpl
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileRemoteDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@ContributesTo(DataScope::class)
interface DatabaseProviders {
    @SingleIn(DataScope::class)
    @Provides
    private fun provideRoomDatabase(
        helper: DatabaseHelper,
        decryptedPasswordConverters: DecryptedPasswordConverters,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): ComicViewerDatabase = helper
        .getDatabaseBuilder()
        .addMigrations(ComicViewerDatabase.ManualMigration7to8())
        .addTypeConverter(decryptedPasswordConverters)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(dispatcher)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .build()

    @SingleIn(DataScope::class)
    @Provides
    private fun provideBookshelfDao(database: ComicViewerDatabase): BookshelfDao = database
        .bookshelfDao()

    @SingleIn(DataScope::class)
    @Provides
    private fun provideFileDao(database: ComicViewerDatabase): FileDao = database.fileDao()

    @SingleIn(DataScope::class)
    @Provides
    private fun provideCollectionDao(database: ComicViewerDatabase): CollectionDao = database
        .collectionDao()

    @SingleIn(DataScope::class)
    @Provides
    private fun provideCollectionFileDao(database: ComicViewerDatabase): CollectionFileDao =
        database.collectionFileDao()

    @SingleIn(DataScope::class)
    @Provides
    private fun provideReadLaterFileDao(database: ComicViewerDatabase): ReadLaterFileDao = database
        .readLaterFileDao()

    @IoDispatcher
    @SingleIn(DataScope::class)
    @Provides
    @Suppress("InjectDispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Binds
    private fun BookshelfLocalDataSourceImpl.bind(): BookshelfLocalDataSource = this

    @Binds
    private fun CollectionFileLocalDataSourceImpl.bind(): CollectionFileLocalDataSource = this

    @Binds
    private fun CollectionLocalDataSourceImpl.bind(): CollectionLocalDataSource = this

    @Binds
    private fun FileLocalDataSourceImpl.bind(): FileLocalDataSource = this

    @Binds
    private fun FileRemoteDataSourceImpl.bind(): FileRemoteDataSource = this

    @Binds
    private fun ReadLaterFileLocalDataSourceImpl.bind(): ReadLaterFileLocalDataSource = this
}
