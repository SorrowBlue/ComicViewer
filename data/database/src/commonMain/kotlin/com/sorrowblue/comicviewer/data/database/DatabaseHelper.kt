package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase
import org.koin.core.annotation.Singleton

internal expect class DatabaseHelper {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}
