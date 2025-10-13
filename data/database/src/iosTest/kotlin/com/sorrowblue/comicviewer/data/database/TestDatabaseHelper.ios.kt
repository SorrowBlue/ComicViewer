package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import jakarta.inject.Singleton

@Singleton
internal actual class TestDatabaseHelper {

    actual fun getDatabaseBuilder() = Room.inMemoryDatabaseBuilder<ComicViewerDatabase>()
}
