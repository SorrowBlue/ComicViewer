package com.sorrowblue.comicviewer.data.database

import androidx.room3.RoomDatabase
import dev.zacsweers.metro.Inject

@Inject
internal expect class TestDatabaseHelper() {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}
