package com.sorrowblue.comicviewer.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

internal fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<ComicViewerDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<ComicViewerDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
