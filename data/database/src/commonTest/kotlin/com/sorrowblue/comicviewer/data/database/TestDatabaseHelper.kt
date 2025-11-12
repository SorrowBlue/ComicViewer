package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase
import dev.zacsweers.metro.Inject

@Inject
internal expect class TestDatabaseHelper() {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}
