package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.TestDatabaseHelper
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.KoinConfiguration

@Module
@ComponentScan("com.sorrowblue.comicviewer.data.database")
internal class DatabaseTestModule

internal fun getTestRoomDatabase(
    helper: TestDatabaseHelper,
    decryptedPasswordConverters: DecryptedPasswordConverters,
): ComicViewerDatabase {
    return helper.getDatabaseBuilder()
        .addMigrations(ComicViewerDatabase.ManualMigration7to8())
        .addTypeConverter(decryptedPasswordConverters)
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .build()
}

internal expect fun configurationKoinForTest(): KoinConfiguration
