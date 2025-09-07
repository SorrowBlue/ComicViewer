package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase
import org.koin.core.annotation.Single

@Single
internal expect class TestDatabaseHelper() {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}
