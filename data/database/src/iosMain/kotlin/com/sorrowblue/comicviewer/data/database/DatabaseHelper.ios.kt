package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.annotation.Singleton
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Singleton
internal actual class DatabaseHelper {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val dbFilePath = documentDirectory() + "/$DATABASE_NAME"
        return Room.databaseBuilder<ComicViewerDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
    }

    private fun documentDirectory(): String {
        @OptIn(ExperimentalForeignApi::class)
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}
