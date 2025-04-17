package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase

internal expect class DatabaseHelper {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}
