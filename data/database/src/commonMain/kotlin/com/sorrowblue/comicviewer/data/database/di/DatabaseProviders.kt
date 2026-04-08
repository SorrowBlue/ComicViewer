package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.DatabaseHelper
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
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
        .addMigrations(
            ComicViewerDatabase.ManualMigration7to8(),
            ComicViewerDatabase.ManualMigration8to9(),
            ComicViewerDatabase.ManualMigration9to10(),
        )
        .addTypeConverter(decryptedPasswordConverters)
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
}
