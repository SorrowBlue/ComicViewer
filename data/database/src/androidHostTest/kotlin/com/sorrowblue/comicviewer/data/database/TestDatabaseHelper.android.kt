package com.sorrowblue.comicviewer.data.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import dev.zacsweers.metro.Inject

@Inject
internal actual class TestDatabaseHelper {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        return Room.inMemoryDatabaseBuilder<ComicViewerDatabase>()
    }
}
