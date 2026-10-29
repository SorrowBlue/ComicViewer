/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.DatabaseHelper
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@ContributesTo(AppScope::class)
@BindingContainer
object DatabaseProviders {
    @SingleIn(AppScope::class)
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
        .addColumnTypeConverter(decryptedPasswordConverters)
        .setQueryCoroutineContext(dispatcher)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .build()

    @SingleIn(AppScope::class)
    @Provides
    private fun ComicViewerDatabase.provideBookshelfDao(): BookshelfDao = bookshelfDao()

    @SingleIn(AppScope::class)
    @Provides
    private fun ComicViewerDatabase.provideFileDao(): FileDao = fileDao()

    @SingleIn(AppScope::class)
    @Provides
    private fun ComicViewerDatabase.provideCollectionDao(): CollectionDao = collectionDao()

    @SingleIn(AppScope::class)
    @Provides
    private fun ComicViewerDatabase.provideCollectionFileDao(): CollectionFileDao =
        collectionFileDao()

    @SingleIn(AppScope::class)
    @Provides
    private fun ComicViewerDatabase.provideReadLaterFileDao(): ReadLaterFileDao = readLaterFileDao()

    @IoDispatcher
    @SingleIn(AppScope::class)
    @Provides
    @Suppress("InjectDispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
