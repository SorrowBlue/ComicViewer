package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import logcat.logcat
import jakarta.inject.Singleton

@Singleton
internal actual class DatabaseHelper actual constructor(private val context: PlatformContext) {

    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val dbPath = context.filesDir.resolve("database").also {
            it.createDirectories()
            logcat { "getDatabaseBuilder path=${it.absolutePathString()}" }
        }
        return Room.databaseBuilder<ComicViewerDatabase>(
            name = dbPath.resolve(DATABASE_NAME).absolutePathString()
        )
            .setDriver(BundledSQLiteDriver())
    }
}
