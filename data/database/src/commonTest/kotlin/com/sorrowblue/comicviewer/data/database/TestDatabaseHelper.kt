package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase

internal expect class TestDatabaseHelper() {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}
