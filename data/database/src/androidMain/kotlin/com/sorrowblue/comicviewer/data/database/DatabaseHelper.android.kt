package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject

@Inject
internal actual class DatabaseHelper actual constructor(private val context: PlatformContext) {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(DatabaseName)
        return Room
            .databaseBuilder<ComicViewerDatabase>(
                context = appContext,
                name = dbFile.absolutePath,
            ).setDriver(BundledSQLiteDriver())
    }
}
