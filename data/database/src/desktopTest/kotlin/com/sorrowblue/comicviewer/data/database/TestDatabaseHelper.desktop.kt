package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.annotation.Singleton

@Singleton
internal actual class TestDatabaseHelper {

    actual fun getDatabaseBuilder() =
        Room.inMemoryDatabaseBuilder<ComicViewerDatabase>()
            .setDriver(BundledSQLiteDriver())
}
