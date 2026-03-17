package com.sorrowblue.comicviewer.data.database

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.zacsweers.metro.Inject

@Inject
internal actual class TestDatabaseHelper {
    actual fun getDatabaseBuilder() = Room
        .inMemoryDatabaseBuilder<ComicViewerDatabase>()
        .setDriver(BundledSQLiteDriver())
}
