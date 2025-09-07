package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.platform.app.InstrumentationRegistry
import org.koin.core.annotation.Single

@Single
internal actual class TestDatabaseHelper {

    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val context = InstrumentationRegistry.getInstrumentation().context
        return Room.inMemoryDatabaseBuilder<ComicViewerDatabase>(context)
    }
}
