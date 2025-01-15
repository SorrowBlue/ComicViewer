package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import org.koin.core.annotation.Singleton

@Singleton
internal actual class DatabaseHelper {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), DATABASE_NAME)
        return Room.databaseBuilder<ComicViewerDatabase>(name = dbFile.absolutePath)
    }
}
