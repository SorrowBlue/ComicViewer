package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase
import jakarta.inject.Singleton

@Singleton
internal expect class TestDatabaseHelper() {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}
