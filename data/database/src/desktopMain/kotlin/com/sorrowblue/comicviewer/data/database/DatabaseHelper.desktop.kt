package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.sorrowblue.comicviewer.framework.common.getAppSpecificConfigDirectory
import kotlin.io.path.absolutePathString
import logcat.logcat
import org.koin.core.annotation.Singleton

@Singleton
internal actual class DatabaseHelper {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val dbPath = getAppSpecificConfigDirectory("db").also {
            logcat { "getDatabaseBuilder path=${it.absolutePathString()}" }
        }
        return Room.databaseBuilder<ComicViewerDatabase>(name = dbPath.resolve(DATABASE_NAME).absolutePathString())
    }
}
