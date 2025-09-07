package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import org.koin.core.annotation.Single

@Single
internal actual class TestDatabaseHelper {

    actual fun getDatabaseBuilder() = Room.inMemoryDatabaseBuilder<ComicViewerDatabase>()
}
