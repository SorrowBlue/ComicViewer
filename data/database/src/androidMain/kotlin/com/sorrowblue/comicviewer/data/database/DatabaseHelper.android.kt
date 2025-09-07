package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import org.koin.core.annotation.Single

@Single
internal actual class DatabaseHelper actual constructor(private val context: PlatformContext) {

    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<ComicViewerDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
