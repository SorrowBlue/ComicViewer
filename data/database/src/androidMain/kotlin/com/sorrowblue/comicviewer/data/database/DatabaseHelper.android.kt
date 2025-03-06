package com.sorrowblue.comicviewer.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import org.koin.core.annotation.Singleton

@Singleton
internal actual class DatabaseHelper(
    private val context: Context,
) {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<ComicViewerDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        ).setDriver(AndroidSQLiteDriver())
    }
}
