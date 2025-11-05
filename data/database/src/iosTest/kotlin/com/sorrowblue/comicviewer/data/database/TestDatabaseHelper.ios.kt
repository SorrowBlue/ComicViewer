package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import jakarta.inject.Inject

@Inject
internal actual class TestDatabaseHelper {
    actual fun getDatabaseBuilder() = Room.inMemoryDatabaseBuilder<ComicViewerDatabase>()
}
